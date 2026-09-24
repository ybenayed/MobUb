package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.PassageTBDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassageTBService {

    private final RestTemplate restTemplate;
    private final LineTBService lineTBService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MONITORING_URL =
        "https://bdx.mecatran.com/utw/ws/siri/2.0/bordeaux/stop-monitoring.json" +
        "?AccountKey=opendata-bordeaux-metropole-flux-gtfs-rt&MonitoringRef={stopId}";

    // Fuseau horaire local (Bordeaux) et formatteur d'heure HH:mm
    private static final ZoneId BORDEAUX_ZONE = ZoneId.of("Europe/Paris");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final long CACHE_TTL_SECONDS = 20;

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    private record CacheEntry(List<PassageTBDTO> passages, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    public List<PassageTBDTO> getNextPassages(String stopId) {
        CacheEntry entry = cache.get(stopId);
        if (entry != null && !entry.isExpired()) {
            return entry.passages();
        }

        ReentrantLock lock = locks.computeIfAbsent(stopId, k -> new ReentrantLock());
        lock.lock();
        try {
            entry = cache.get(stopId);
            if (entry != null && !entry.isExpired()) {
                return entry.passages();
            }

            List<PassageTBDTO> fresh = fetchFromApi(stopId);
            cache.put(stopId, new CacheEntry(fresh, Instant.now().plusSeconds(CACHE_TTL_SECONDS)));
            return fresh;

        } finally {
            lock.unlock();
        }
    }

    private List<PassageTBDTO> fetchFromApi(String stopId) {
        String url = UriComponentsBuilder.fromUriString(MONITORING_URL)
            .buildAndExpand(stopId)
            .toUriString();

        String rawJson = restTemplate.getForObject(url, String.class);
        List<PassageTBDTO> passages = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode deliveries = root.path("Siri").path("ServiceDelivery").path("StopMonitoringDelivery");
            if (!deliveries.isArray() || deliveries.isEmpty()) return passages;

            for (JsonNode visit : deliveries.get(0).path("MonitoredStopVisit")) {
                JsonNode journey = visit.path("MonitoredVehicleJourney");
                JsonNode call = journey.path("MonitoredCall");

                String aimedRaw = call.path("AimedArrivalTime").asText(null);
                String expectedRaw = call.path("ExpectedArrivalTime").asText(null);

                String heureTheorique = formatToBordeauxTime(aimedRaw);
                String heurePrevue = formatToBordeauxTime(expectedRaw);

                Long retardSecondes = null;
                if (aimedRaw != null && expectedRaw != null) {
                    retardSecondes = Duration.between(Instant.parse(aimedRaw), Instant.parse(expectedRaw)).getSeconds();
                }

                passages.add(PassageTBDTO.builder()
                        .ligne(resolveLigne(journey))
                        .direction(firstValue(journey.path("DirectionName")))
                        .destination(firstValue(journey.path("DestinationName")))
                        .heureTheorique(heureTheorique)
                        .heurePrevue(heurePrevue)
                        .retardSecondes(retardSecondes)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur recuperation passages pour " + stopId, e);
        }

        log.info("Passages rafraichis pour {} : {} resultats", stopId, passages.size());
        return passages;
    }

    /**
     * Convertit une chaîne ISO-8601 UTC 
     * vers l'heure locale de Bordeaux au format "HH:mm" 
     */
    private String formatToBordeauxTime(String isoUtcString) {
        if (isoUtcString == null || isoUtcString.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(isoUtcString)
                    .atZone(BORDEAUX_ZONE)
                    .format(TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("Impossible de parser la date ISO : {}", isoUtcString);
            return isoUtcString;
        }
    }

    private String resolveLigne(JsonNode journey) {
        String lineRef = journey.path("LineRef").path("value").asText(null);

        String code = lineTBService.resolveLineCode(lineRef);
        if (code != null && !code.isBlank()) {
            return code;
        }

        return extractLineCodeFromRef(lineRef);
    }

    private String extractLineCodeFromRef(String lineRef) {
        if (lineRef == null || lineRef.isBlank()) return null;
        if (!lineRef.contains(":")) return lineRef;

        String[] segments = lineRef.split(":");
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equalsIgnoreCase("line") && i + 1 < segments.length) {
                return segments[i + 1];
            }
        }
        return segments.length >= 2 ? segments[segments.length - 2] : segments[segments.length - 1];
    }

    private String firstValue(JsonNode arrayNode) {
        if (arrayNode.isArray() && !arrayNode.isEmpty()) {
            return arrayNode.get(0).path("value").asText(null);
        }
        return null;
    }
}