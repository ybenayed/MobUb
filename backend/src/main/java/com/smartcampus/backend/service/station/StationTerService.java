package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.station.StationPositionTerDTO;
import com.smartcampus.backend.dto.station.StationTerDTO;
import com.smartcampus.backend.dto.station.StationTerRequestDTO;
import com.smartcampus.backend.entity.station.StationTer;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.mapper.station.StationTerMapper;
import com.smartcampus.backend.repository.station.StationTerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Partie STATIQUE : positions des gares TER autour de Bordeaux.
 * Ces donnees ne changent quasiment jamais -> on les importe UNE FOIS en base
 * via /import (idempotent), et le front lit ensuite en base (pas d'appel
 * Navitia a chaque chargement de carte).
 */
@Slf4j
@Service
public class StationTerService {

    private final StationTerRepository stationTerRepository;
    private final StationTerMapper stationTerMapper;

    public StationTerService(
            StationTerRepository stationTerRepository,
            StationTerMapper stationTerMapper,
            @Qualifier("navitiaRestTemplate") RestTemplate restTemplate) {
        this.stationTerRepository = stationTerRepository;
        this.stationTerMapper = stationTerMapper;
        this.restTemplate = restTemplate;
    }
    
    @Qualifier("navitiaRestTemplate")
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${navitia.base-url}")
    private String navitiaBaseUrl;

    @Value("${navitia.center-lat}")
    private double centerLat;

    @Value("${navitia.center-lon}")
    private double centerLon;

    @Value("${navitia.radius-meters:20000}")
    private int radiusMeters;

    private static final String PLACES_NEARBY_TEMPLATE =
        "{baseUrl}/coords/{lon};{lat}/places_nearby?distance={distance}&type[]=stop_area&count=100";

    // ─── LECTURE (utilisee par le front, pas de quota consomme)

    public List<StationTerDTO> getAllStations() {
        return stationTerRepository.findAll().stream().map(stationTerMapper::toDTO).toList();
    }

    public List<StationPositionTerDTO> getAllPositions() {
        return stationTerRepository.findAllPositions();
    }

    // ─── IMPORT DEPUIS NAVITIA (idempotent, a lancer manuellement/rarement)

    public int importStationsFromApi() {
        String url = UriComponentsBuilder.fromUriString(PLACES_NEARBY_TEMPLATE)
                .buildAndExpand(navitiaBaseUrl, centerLon, centerLat, radiusMeters)
                .toUriString();

        String rawJson = restTemplate.getForObject(url, String.class);
        int imported = 0;

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode nearby = root.path("places_nearby");

            int total = root.path("pagination").path("total_result").asInt(0);
            int onPage = root.path("pagination").path("items_on_page").asInt(0);
            if (total > onPage) {
                log.warn("Pagination Navitia detectee ({} resultats, {} recus) : "
                        + "augmenter le rayon de recherche recupere une partie seulement", total, onPage);
            }

            for (JsonNode item : nearby) {
                JsonNode stopArea = item.path("stop_area");
                String navitiaId = stopArea.path("id").asText(null);
                if (navitiaId == null || stationTerRepository.existsByNavitiaId(navitiaId)) {
                    continue; // idempotent : on ne recree pas une gare deja en base
                }

                stationTerRepository.save(buildStationFromRecord(stopArea, item));
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur import gares TER", e);
        }

        log.info("Import gares TER termine : {} nouvelles gares", imported);
        return imported;
    }

    private StationTer buildStationFromRecord(JsonNode stopArea, JsonNode nearbyItem) {
        JsonNode coord = stopArea.path("coord");
        Double lat = coord.hasNonNull("lat") ? coord.get("lat").asDouble() : null;
        Double lon = coord.hasNonNull("lon") ? coord.get("lon").asDouble() : null;

        Point point = null;
        if (lat != null && lon != null) {
            point = geometryFactory.createPoint(new Coordinate(lon, lat));
        }

        Double distance = nearbyItem.hasNonNull("distance")
                ? nearbyItem.get("distance").asDouble()
                : null;

        return StationTer.builder()
                .navitiaId(stopArea.path("id").asText(null))
                .nom(stopArea.path("name").asText(null))
                .latitude(lat)
                .longitude(lon)
                .location(point)
                .distanceCentreMetres(distance)
                .build();
    }

    public StationTerDTO createStation(StationTerRequestDTO request) {
    if (stationTerRepository.existsByNavitiaId(request.getNavitiaId())) {
        throw new IllegalArgumentException("Une gare avec cet identifiant existe deja : " + request.getNavitiaId());
    }
    StationTer station = StationTer.builder()
            .navitiaId(request.getNavitiaId())
            .nom(request.getNom())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .location(toPoint(request.getLatitude(), request.getLongitude()))
            .build();
    return stationTerMapper.toDTO(stationTerRepository.save(station));
}

public StationTerDTO updateStation(Long id, StationTerRequestDTO request) {
    StationTer station = stationTerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Gare introuvable : " + id));
    station.setNom(request.getNom());
    station.setLatitude(request.getLatitude());
    station.setLongitude(request.getLongitude());
    station.setLocation(toPoint(request.getLatitude(), request.getLongitude()));
    return stationTerMapper.toDTO(stationTerRepository.save(station));
}

public void deleteStation(Long id) {
    if (!stationTerRepository.existsById(id)) {
        throw new ResourceNotFoundException("Gare introuvable : " + id);
    }
    stationTerRepository.deleteById(id);
}

private Point toPoint(Double lat, Double lon) {
    if (lat == null || lon == null) return null;
    return geometryFactory.createPoint(new Coordinate(lon, lat));
}
}