package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.StationVDTO;
import com.smartcampus.backend.dto.station.StationVRequestDTO;
import com.smartcampus.backend.dto.station.StationPositionVDTO;
import com.smartcampus.backend.entity.station.StationV;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.mapper.station.StationVMapper;
import com.smartcampus.backend.repository.station.StationVRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationVService {

    private final StationVRepository stationVRepository;
    private final StationVMapper stationVMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static final String STATION_INFORMATION_URL =
        "https://bdx.mecatran.com/utw/ws/gbfs/bordeaux/v3/station_information.json" +
        "?apiKey=opendata-bordeaux-metropole-flux-gtfs-rt";


    public List<StationVDTO> getAllStations() {
        return stationVRepository.findAll().stream().map(stationVMapper::toDTO).toList();
    }

    public List<StationPositionVDTO> getAllPositions() {
        return stationVRepository.findAllPositions();
    }

    public Optional<StationV> getByStationId(String stationId) {
        return stationVRepository.findByStationId(stationId);
    }


    public int importStationsFromApi() {
        int imported = 0;
        String rawJson = restTemplate.getForObject(STATION_INFORMATION_URL, String.class);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode stations = root.path("data").path("stations");

            for (JsonNode station : stations) {
                String stationId = station.path("station_id").asText(null);
                if (stationId == null || stationVRepository.existsByStationId(stationId)) {
                    continue; 
                }

                stationVRepository.save(buildStationFromRecord(station));
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur import stations velo", e);
        }

        log.info("Import stations velo termine : {} nouvelles stations", imported);
        return imported;
    }

    private StationV buildStationFromRecord(JsonNode station) {
        Double lat = station.hasNonNull("lat") ? station.get("lat").asDouble() : null;
        Double lon = station.hasNonNull("lon") ? station.get("lon").asDouble() : null;

        Point point = null;
        if (lat != null && lon != null) {
            point = geometryFactory.createPoint(new Coordinate(lon, lat));
        }

        String nom = null;
        JsonNode nameArray = station.path("name");
        if (nameArray.isArray() && !nameArray.isEmpty()) {
            nom = nameArray.get(0).path("text").asText(null);
        }

        return StationV.builder()
                .stationId(station.path("station_id").asText(null))
                .nom(nom)
                .adresse(station.path("address").asText(null))
                .capacite(station.hasNonNull("capacity") ? station.get("capacity").asInt() : null)
                .latitude(lat)
                .longitude(lon)
                .location(point)
                .build();
    }


    public StationVDTO createStation(StationVRequestDTO request) {
        if (stationVRepository.existsByStationId(request.getStationId())) {
            throw new IllegalArgumentException("Une station avec cet identifiant existe deja : " + request.getStationId());
        }
        StationV station = StationV.builder()
                .stationId(request.getStationId())
                .nom(request.getNom())
                .adresse(request.getAdresse())
                .capacite(request.getCapacite())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(toPoint(request.getLatitude(), request.getLongitude()))
                .build();
        return stationVMapper.toDTO(stationVRepository.save(station));
    }

    public StationVDTO updateStation(Long id, StationVRequestDTO request) {
        StationV station = stationVRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station velo introuvable : " + id));

        // Vérifier si le stationId est déjà utilisé par une *autre* station
        Optional<StationV> existingWithSameStationId = stationVRepository.findByStationId(request.getStationId());
        if (existingWithSameStationId.isPresent() && !existingWithSameStationId.get().getId().equals(id)) {
            throw new IllegalArgumentException("Une autre station utilise déjà l'identifiant : " + request.getStationId());
        }

        station.setStationId(request.getStationId());
        station.setNom(request.getNom());
        station.setAdresse(request.getAdresse());
        station.setCapacite(request.getCapacite());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        station.setLocation(toPoint(request.getLatitude(), request.getLongitude()));

        return stationVMapper.toDTO(stationVRepository.save(station));
    }

    public void deleteStation(Long id) {
        if (!stationVRepository.existsById(id)) {
            throw new ResourceNotFoundException("Station velo introuvable : " + id);
        }
        stationVRepository.deleteById(id);
    }

    private Point toPoint(Double lat, Double lon) {
        if (lat == null || lon == null) return null;
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }


    public List<StationVDTO> searchStations(String query) {
        List<StationV> stations = (query == null || query.isBlank())
                ? stationVRepository.findAll()
                : stationVRepository.findByNomContainingIgnoreCase(query);
        return stations.stream().map(stationVMapper::toDTO).toList();
    }
}