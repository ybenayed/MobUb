package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.StationTBDTO;
import com.smartcampus.backend.dto.station.StationTBRequestDTO;
import com.smartcampus.backend.dto.station.StationPositionTBDTO;
import com.smartcampus.backend.entity.station.StationTB;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.mapper.station.StationTBMapper;
import com.smartcampus.backend.repository.station.StationTBRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationTBService {

    private final StationTBRepository stationTBRepository;
    private final StationTBMapper   stationTBMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static final String DISCOVERY_URL =
        "https://bdx.mecatran.com/utw/ws/siri/2.0/bordeaux/stoppoints-discovery.json" +
        "?AccountKey=opendata-bordeaux-metropole-flux-gtfs-rt";

    // ─── LECTURE

    public List<StationTBDTO> getAllStations() {
        return stationTBRepository.findAll().stream().map(stationTBMapper::toDTO).toList();
    }

    public List<StationPositionTBDTO> getAllPositions() {
        return stationTBRepository.findAllPositions();
    }

    public List<StationPositionTBDTO> getPositionsByMode(String mode) {
        return stationTBRepository.findPositionsByMode(mode);
    }

    // ─── IMPORT DEPUIS L'API (idempotent) - un seul appel, pas de pagination ici

    public int importStationsFromApi() {
        int imported = 0;
        String rawJson = restTemplate.getForObject(DISCOVERY_URL, String.class);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode stops = root.path("Siri").path("StopPointsDelivery").path("AnnotatedStopPointRef");

            for (JsonNode stop : stops) {
                String stopId = stop.path("StopPointRef").path("value").asText(null);
                if (stopId == null || stationTBRepository.existsByStopId(stopId)) {
                    continue; // idempotent
                }

                stationTBRepository.save(buildStationFromRecord(stop));
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur import stations", e);
        }

        log.info("Import stations termine : {} nouvelles stations", imported);
        return imported;
    }

    private StationTB buildStationFromRecord(JsonNode stop) {
        JsonNode location = stop.path("Location");
        Double lat = location.hasNonNull("latitude") ? location.get("latitude").asDouble() : null;
        Double lon = location.hasNonNull("longitude") ? location.get("longitude").asDouble() : null;

        Point point = null;
        if (lat != null && lon != null) {
            point = geometryFactory.createPoint(new Coordinate(lon, lat));
        }

        List<String> lines = new ArrayList<>();
        for (JsonNode line : stop.path("Lines")) {
            String value = line.path("value").asText(null);
            if (value != null) lines.add(value);
        }

        String stopAreaRef = stop.path("StopAreaRef").path("value").asText(null);

        return StationTB.builder()
                .stopId(stop.path("StopPointRef").path("value").asText(null))
                .nom(stop.path("StopName").path("value").asText(null))
                .stopAreaRef(stopAreaRef)
                .mode(detectMode(stopAreaRef))
                .latitude(lat)
                .longitude(lon)
                .location(point)
                .lines(lines)
                .build();
    }

    // Convention observee : bordeaux:StopPoint:BP:T....:LOC -> Tram
    //                        bordeaux:StopPoint:BP:B....:LOC -> Bus
    private String detectMode(String stopAreaRef) {
        if (stopAreaRef == null) return "INCONNU";
        String[] parts = stopAreaRef.split(":");
        if (parts.length < 4) return "INCONNU";
        String code = parts[3];
        if (code.startsWith("T")) return "TRAM";
        if (code.startsWith("B")) return "BUS";
        return "INCONNU";
    }


    public StationTBDTO createStation(StationTBRequestDTO request) {
    if (stationTBRepository.existsByStopId(request.getStopId())) {
        throw new IllegalArgumentException("Un arret avec cet identifiant existe deja : " + request.getStopId());
    }
    String mode = (request.getMode() != null) ? request.getMode() : detectMode(request.getStopAreaRef());
    StationTB station = StationTB.builder()
            .stopId(request.getStopId())
            .nom(request.getNom())
            .stopAreaRef(request.getStopAreaRef())
            .mode(mode)
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .location(toPoint(request.getLatitude(), request.getLongitude()))
            .lines(request.getLines())
            .build();
    return stationTBMapper.toDTO(stationTBRepository.save(station));
}

public StationTBDTO updateStation(Long id, StationTBRequestDTO request) {
    StationTB station = stationTBRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Arret introuvable : " + id));
    station.setNom(request.getNom());
    station.setStopAreaRef(request.getStopAreaRef());
    station.setMode(request.getMode() != null ? request.getMode() : detectMode(request.getStopAreaRef()));
    station.setLatitude(request.getLatitude());
    station.setLongitude(request.getLongitude());
    station.setLocation(toPoint(request.getLatitude(), request.getLongitude()));
    station.setLines(request.getLines());
    return stationTBMapper.toDTO(stationTBRepository.save(station));
}

public void deleteStation(Long id) {
    if (!stationTBRepository.existsById(id)) {
        throw new ResourceNotFoundException("Arret introuvable : " + id);
    }
    stationTBRepository.deleteById(id);
}

private Point toPoint(Double lat, Double lon) {
    if (lat == null || lon == null) return null;
    return geometryFactory.createPoint(new Coordinate(lon, lat));
}
}