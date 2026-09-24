package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.PassageTerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Service
public class PassageTerService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${navitia.base-url}")
    private String navitiaBaseUrl;

    @Value("${navitia.cache-ttl-seconds:60}")
    private long cacheTtlSeconds;

    private static final ZoneId BORDEAUX_ZONE = ZoneId.of("Europe/Paris");
    private static final DateTimeFormatter NAVITIA_DATETIME = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final String DEPARTURES_TEMPLATE =
            "{baseUrl}/stop_areas/{stopId}/departures?count=10&data_freshness=realtime";

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public PassageTerService(@Qualifier("navitiaRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private record CacheEntry(List<PassageTerDTO> passages, long expiresAtEpochMs) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAtEpochMs;
        }
    }

    public List<PassageTerDTO> getNextPassages(String navitiaStopId) {
        CacheEntry entry = cache.get(navitiaStopId);
        if (entry != null && !entry.isExpired()) {
            return entry.passages();
        }

        ReentrantLock lock = locks.computeIfAbsent(navitiaStopId, k -> new ReentrantLock());
        lock.lock();
        try {
            entry = cache.get(navitiaStopId);
            if (entry != null && !entry.isExpired()) {
                return entry.passages(); 
            }

            List<PassageTerDTO> fresh = fetchFromApi(navitiaStopId);
            cache.put(navitiaStopId, new CacheEntry(fresh,
                    System.currentTimeMillis() + cacheTtlSeconds * 1000));
            return fresh;

        } finally {
            lock.unlock();
        }
    }

    /** Utilise par le scheduler pour prechauffer le cache sans dupliquer la logique de fetch. */
    public void refreshCache(String navitiaStopId) {
        List<PassageTerDTO> fresh = fetchFromApi(navitiaStopId);
        cache.put(navitiaStopId, new CacheEntry(fresh,
                System.currentTimeMillis() + cacheTtlSeconds * 1000));
    }

    private List<PassageTerDTO> fetchFromApi(String navitiaStopId) {
        String url = UriComponentsBuilder.fromUriString(DEPARTURES_TEMPLATE)
                .buildAndExpand(navitiaBaseUrl, navitiaStopId)
                .toUriString();

        List<PassageTerDTO> passages = new ArrayList<>();
        try {
            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            for (JsonNode dep : root.path("departures")) {
                JsonNode info = dep.path("display_informations");
                JsonNode stopDateTime = dep.path("stop_date_time");

                String aimedRaw = stopDateTime.path("base_departure_date_time").asText(null);
                String expectedRaw = stopDateTime.path("departure_date_time").asText(null);
                boolean tempsReel = "realtime".equals(stopDateTime.path("data_freshness").asText(null));

                String heureTheorique = formatToBordeauxTime(aimedRaw);
                String heurePrevue = formatToBordeauxTime(expectedRaw);

                Long retardSecondes = null;
                if (tempsReel && aimedRaw != null && expectedRaw != null) {
                    retardSecondes = computeDelaySeconds(aimedRaw, expectedRaw);
                }

                passages.add(PassageTerDTO.builder()
                        .ligne(info.path("code").asText(null))
                        .modeCommercial(info.path("commercial_mode").asText(null))
                        .direction(info.path("direction").asText(null))
                        .destination(info.path("headsign").asText(null))
                        .heureTheorique(heureTheorique)
                        .heurePrevue(heurePrevue)
                        .retardSecondes(retardSecondes)
                        .tempsReel(tempsReel)
                        .build());
            }
        } catch (HttpStatusCodeException e) {
            log.error("Navitia a repondu {} pour {} : {}",
                    e.getStatusCode(), navitiaStopId, e.getResponseBodyAsString());
            throw new RuntimeException("Navitia HTTP " + e.getStatusCode() + " pour " + navitiaStopId, e);
        } catch (Exception e) {
            log.error("Erreur recuperation passages pour {}", navitiaStopId, e);
            throw new RuntimeException("Erreur recuperation passages pour " + navitiaStopId, e);
        }

        long nbTempsReel = passages.stream().filter(PassageTerDTO::isTempsReel).count();
        log.info("Passages rafraichis pour {} : {} resultats ({} en temps reel)",
                navitiaStopId, passages.size(), nbTempsReel);
        return passages;
    }


    private String formatToBordeauxTime(String navitiaDateTimeStr) {
        if (navitiaDateTimeStr == null || navitiaDateTimeStr.isBlank()) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(navitiaDateTimeStr, NAVITIA_DATETIME);
            return ldt.atZone(BORDEAUX_ZONE).format(TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Impossible de parser la date Navitia : {}", navitiaDateTimeStr);
            return navitiaDateTimeStr;
        }
    }

    private Long computeDelaySeconds(String aimed, String expected) {
        try {
            LocalDateTime aimedDt = LocalDateTime.parse(aimed, NAVITIA_DATETIME);
            LocalDateTime expectedDt = LocalDateTime.parse(expected, NAVITIA_DATETIME);
            return Duration.between(aimedDt, expectedDt).getSeconds();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}