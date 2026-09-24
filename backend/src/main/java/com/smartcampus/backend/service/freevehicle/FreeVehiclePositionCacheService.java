package com.smartcampus.backend.service.freevehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.freevehicle.FreeVehiclePositionDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeVehiclePositionCacheService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String FREE_BIKE_STATUS_URL =
        "https://services.rideyego.com/gbfs/2-2/bordeaux/fr/free_bike_status";

    // cache thread-safe, mis a jour toutes les 10s 
    private final AtomicReference<List<JsonNode>> cache = new AtomicReference<>(Collections.emptyList());

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedRate = 10000)
    public void refresh() {
        try {
            String rawJson = restTemplate.getForObject(FREE_BIKE_STATUS_URL, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            List<JsonNode> vehicles = new ArrayList<>();
            for (JsonNode bike : root.path("data").path("bikes")) {
                vehicles.add(bike);
            }

            cache.set(vehicles);
            log.info("Cache vehicules rafraichi : {} vehicules", vehicles.size());
        } catch (Exception e) {
            log.error("Erreur refresh cache vehicules, ancien cache conserve", e);
        }
    }

    //  (jamais d'appel HTTP ici, juste lecture du cache)

    public List<FreeVehiclePositionDTO> getAllPositions() {
        return cache.get().stream().map(this::toPositionDTO).toList();
    }

    public List<FreeVehiclePositionDTO> getPositionsByType(String vehicleTypeId) {
        return cache.get().stream()
                .filter(v -> vehicleTypeId.equalsIgnoreCase(v.path("vehicle_type_id").asText(null)))
                .map(this::toPositionDTO)
                .toList();
    }

    public JsonNode getRawVehicle(String bikeId) {
        return cache.get().stream()
                .filter(v -> bikeId.equals(v.path("bike_id").asText(null)))
                .findFirst()
                .orElse(null);
    }

    public List<JsonNode> getAllRawVehicles() {
        return cache.get();
    }

    private FreeVehiclePositionDTO toPositionDTO(JsonNode v) {
        return FreeVehiclePositionDTO.builder()
                .bikeId(v.path("bike_id").asText(null))
                .vehicleTypeId(v.path("vehicle_type_id").asText(null))
                .latitude(v.hasNonNull("lat") ? v.get("lat").asDouble() : null)
                .longitude(v.hasNonNull("lon") ? v.get("lon").asDouble() : null)
                .build();
    }
}