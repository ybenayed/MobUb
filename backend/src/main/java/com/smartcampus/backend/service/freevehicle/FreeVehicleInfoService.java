package com.smartcampus.backend.service.freevehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcampus.backend.dto.freevehicle.FreeVehicleDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeCountDTO;
import com.smartcampus.backend.entity.freevehicle.VehicleTypeFV;
import com.smartcampus.backend.repository.freevehicle.VehicleTypeFVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FreeVehicleInfoService {

    private final FreeVehiclePositionCacheService freeVehiclePositionCacheService; 
    private final VehicleTypeFVRepository vehicleTypeFVRepository;                 


    public FreeVehicleDTO getVehicleInfo(String bikeId) {
        JsonNode v = freeVehiclePositionCacheService.getRawVehicle(bikeId);
        if (v == null) return null; 
        return toDetailDTO(v);
    }

    // filtree, elle aussi 100% dynamique

    public List<FreeVehicleDTO> getAllVehicles() {
        return freeVehiclePositionCacheService.getAllRawVehicles().stream()
                .map(this::toDetailDTO)
                .toList();
    }

    public List<FreeVehicleDTO> getVehiclesByType(String vehicleTypeId) {
        return freeVehiclePositionCacheService.getAllRawVehicles().stream()
                .filter(v -> vehicleTypeId.equalsIgnoreCase(v.path("vehicle_type_id").asText(null)))
                .map(this::toDetailDTO)
                .toList();
    }

    // Comptage par type, calcule depuis le cache live + noms depuis le catalogue des types

    public List<VehicleTypeCountDTO> getVehicleCountByType() {
        Map<String, Long> countsByType = new HashMap<>();
        for (JsonNode v : freeVehiclePositionCacheService.getAllRawVehicles()) {
            String typeId = v.path("vehicle_type_id").asText(null);
            if (typeId != null) {
                countsByType.merge(typeId, 1L, Long::sum);
            }
        }

        List<VehicleTypeFV> knownTypes = vehicleTypeFVRepository.findAll();

        return knownTypes.stream()
                .map(type -> VehicleTypeCountDTO.builder()
                        .vehicleTypeId(type.getVehicleTypeId())
                        .name(type.getName())
                        .count(countsByType.getOrDefault(type.getVehicleTypeId(), 0L))
                        .build())
                .toList();
    }


    private FreeVehicleDTO toDetailDTO(JsonNode v) {
        JsonNode rentalUris = v.path("rental_uris");

        String lastReported = v.hasNonNull("last_reported")
                ? Instant.ofEpochSecond(v.get("last_reported").asLong()).toString()
                : null;

        return FreeVehicleDTO.builder()
                .bikeId(v.path("bike_id").asText(null))
                .vehicleTypeId(v.path("vehicle_type_id").asText(null))
                .isReserved(asBooleanOrNull(v.path("is_reserved")))
                .isDisabled(asBooleanOrNull(v.path("is_disabled")))
                .rentalUriAndroid(rentalUris.path("android").asText(null))
                .rentalUriIos(rentalUris.path("ios").asText(null))
                .pricingPlanId(v.path("pricing_plan_id").asText(null))
                .currentRangeMeters(v.hasNonNull("current_range_meters") ? v.get("current_range_meters").asInt() : null)
                .latitude(v.hasNonNull("lat") ? v.get("lat").asDouble() : null)
                .longitude(v.hasNonNull("lon") ? v.get("lon").asDouble() : null)
                .lastReported(lastReported)
                .build();
    }

    private Boolean asBooleanOrNull(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        if (node.isBoolean()) return node.asBoolean();
        if (node.isNumber()) return node.asInt() != 0;
        return null;
    }
}