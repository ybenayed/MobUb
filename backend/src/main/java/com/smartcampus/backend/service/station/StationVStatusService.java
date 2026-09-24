package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.StationVDetailDTO;
import com.smartcampus.backend.dto.station.StationVStatusDTO;
import com.smartcampus.backend.entity.station.StationV;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationVStatusService {

    private final RestTemplate restTemplate;
    private final StationVService stationVService; 
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String STATION_STATUS_URL =
        "https://bdx.mecatran.com/utw/ws/gbfs/bordeaux/v3/station_status.json" +
        "?apiKey=opendata-bordeaux-metropole-flux-gtfs-rt";

    private static final long DEFAULT_TTL_SECONDS = 60; // fallback si le flux ne renvoie pas de ttl

    // ─── CACHE

    private final ReentrantLock refreshLock = new ReentrantLock();
    private volatile Map<String, StationVStatusDTO> cache = new HashMap<>();
    private volatile Instant cacheExpiresAt = Instant.EPOCH; 
    private volatile long lastKnownTtlSeconds = DEFAULT_TTL_SECONDS;

    public Optional<StationVStatusDTO> getStatus(String stationId) {
        ensureCacheFresh();
        return Optional.ofNullable(cache.get(stationId));
    }

    // Rafraichit le cache si expire (thread-safe, evite les appels concurrents en double)
    private void ensureCacheFresh() {
        if (Instant.now().isBefore(cacheExpiresAt)) {
            return; 
        }

        refreshLock.lock();
        try {
            if (Instant.now().isBefore(cacheExpiresAt)) {
                return;
            }
            refreshCache();
        } finally {
            refreshLock.unlock();
        }
    }

    private void refreshCache() {
        try {
            String rawJson = restTemplate.getForObject(STATION_STATUS_URL, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            long ttl = root.hasNonNull("ttl") ? root.get("ttl").asLong() : lastKnownTtlSeconds;
            if (ttl <= 0) ttl = DEFAULT_TTL_SECONDS;

            Map<String, StationVStatusDTO> fresh = new HashMap<>();
            for (JsonNode station : root.path("data").path("stations")) {
                StationVStatusDTO dto = buildStatusFromRecord(station);
                fresh.put(dto.getStationId(), dto);
            }

            cache = fresh;
            lastKnownTtlSeconds = ttl;
            cacheExpiresAt = Instant.now().plusSeconds(ttl);

            log.info("Cache statut velos rafraichi : {} stations, prochain refresh dans {}s", fresh.size(), ttl);
        } catch (Exception e) {
            log.warn("Echec rafraichissement statut velos, on garde le cache existant ({} stations) : {}",
                    cache.size(), e.getMessage());
            cacheExpiresAt = Instant.now().plusSeconds(Math.min(lastKnownTtlSeconds, 15));
        }
    }

    public Optional<StationVDetailDTO> getStationDetail(String stationId) {
        Optional<StationV> staticData = stationVService.getByStationId(stationId);
        if (staticData.isEmpty()) {
            return Optional.empty();
        }

        Optional<StationVStatusDTO> status = getStatus(stationId);
        StationV s = staticData.get();

        StationVDetailDTO.StationVDetailDTOBuilder builder = StationVDetailDTO.builder()
                .stationId(s.getStationId())
                .nom(s.getNom())
                .adresse(s.getAdresse())
                .capacite(s.getCapacite())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude());

        status.ifPresent(st -> builder
                .velosDisponibles(st.getVelosDisponibles())
                .velosClassiques(st.getVelosClassiques())
                .velosElectriques(st.getVelosElectriques())
                .placesDisponibles(st.getPlacesDisponibles())
                .enService(st.getEnService())
                .derniereMaj(st.getDerniereMaj()));

        return Optional.of(builder.build());
    }

    private StationVStatusDTO buildStatusFromRecord(JsonNode station) {
        int classiques = 0;
        int electriques = 0;

        for (JsonNode type : station.path("vehicle_types_available")) {
            String typeId = type.path("vehicle_type_id").asText("");
            int count = type.path("count").asInt(0);
            if ("classic".equals(typeId)) classiques = count;
            if ("electric".equals(typeId)) electriques = count;
        }

        boolean enService = station.path("is_renting").asBoolean(false)
                && station.path("is_returning").asBoolean(false);

        return StationVStatusDTO.builder()
                .stationId(station.path("station_id").asText(null))
                .velosDisponibles(station.path("num_vehicles_available").asInt(0))
                .velosClassiques(classiques)
                .velosElectriques(electriques)
                .placesDisponibles(station.path("num_docks_available").asInt(0))
                .enService(enService)
                .derniereMaj(station.path("last_reported").asText(null))
                .build();
    }
}