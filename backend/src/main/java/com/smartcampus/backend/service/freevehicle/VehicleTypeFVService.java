package com.smartcampus.backend.service.freevehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVRequestDTO;
import com.smartcampus.backend.entity.freevehicle.VehicleTypeFV;
import com.smartcampus.backend.mapper.freevehicle.VehicleTypeFVMapper;
import com.smartcampus.backend.repository.freevehicle.VehicleTypeFVRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleTypeFVService {

    private final VehicleTypeFVRepository vehicleTypeFVRepository;
    private final VehicleTypeFVMapper vehicleTypeFVMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String VEHICLE_TYPES_URL =
        "https://services.rideyego.com/gbfs/2-2/bordeaux/fr/vehicle_types";

    public List<VehicleTypeFVDTO> getAllTypes() {
        return vehicleTypeFVRepository.findAll().stream().map(vehicleTypeFVMapper::toDTO).toList();
    }

    public int importTypesFromApi() {
        int imported = 0;
        String rawJson = restTemplate.getForObject(VEHICLE_TYPES_URL, String.class);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode types = root.path("data").path("vehicle_types");

            for (JsonNode type : types) {
                String vehicleTypeId = type.path("vehicle_type_id").asText(null);
                if (vehicleTypeId == null || vehicleTypeFVRepository.existsByVehicleTypeId(vehicleTypeId)) {
                    continue;
                }

                vehicleTypeFVRepository.save(buildTypeFromRecord(type));
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur import types de vehicules", e);
        }

        log.info("Import types de vehicules termine : {} nouveaux types", imported);
        return imported;
    }

    private VehicleTypeFV buildTypeFromRecord(JsonNode type) {
        Integer maxRange = type.hasNonNull("max_range_meters") ? type.get("max_range_meters").asInt() : null;

        return VehicleTypeFV.builder()
                .vehicleTypeId(type.path("vehicle_type_id").asText(null))
                .formFactor(type.path("form_factor").asText(null))
                .propulsionType(type.path("propulsion_type").asText(null))
                .name(type.path("name").asText(null))
                .maxRangeMeters(maxRange)
                .build();
    }



    public VehicleTypeFVDTO createType(VehicleTypeFVRequestDTO request) {
    if (vehicleTypeFVRepository.existsByVehicleTypeId(request.getVehicleTypeId())) {
        throw new IllegalArgumentException("Ce type existe deja : " + request.getVehicleTypeId());
    }
    VehicleTypeFV type = VehicleTypeFV.builder()
            .vehicleTypeId(request.getVehicleTypeId())
            .formFactor(request.getFormFactor())
            .propulsionType(request.getPropulsionType())
            .name(request.getName())
            .maxRangeMeters(request.getMaxRangeMeters())
            .build();
    return vehicleTypeFVMapper.toDTO(vehicleTypeFVRepository.save(type));
}

public VehicleTypeFVDTO updateType(Long id, VehicleTypeFVRequestDTO request) {
    VehicleTypeFV type = vehicleTypeFVRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Type introuvable : " + id));
    type.setName(request.getName());
    type.setFormFactor(request.getFormFactor());
    type.setPropulsionType(request.getPropulsionType());
    type.setMaxRangeMeters(request.getMaxRangeMeters());
    return vehicleTypeFVMapper.toDTO(vehicleTypeFVRepository.save(type));
}

public void deleteType(Long id) {
    if (!vehicleTypeFVRepository.existsById(id)) {
        throw new RuntimeException("Type introuvable : " + id);
    }
    vehicleTypeFVRepository.deleteById(id);
}
}