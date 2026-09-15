package com.smartcampus.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.dto.CampusUpdateDTO;
import com.smartcampus.backend.entity.Campus;
import com.smartcampus.backend.repository.CampusRepository;
import com.smartcampus.backend.util.GeometryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampusService {

    private final CampusRepository campusRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static final String CAMPUS_FEATURE_NAME = "Campus de Bordeaux";
    private static final String CAMPUS_NAME = "Campus Bordeaux";

    // ─── LECTURE

    public List<CampusDTO> getAllCampus() {
        return campusRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CampusDTO getCampusById(Long id) {
        return campusRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Campus introuvable : " + id));
    }

    // ─── RESET + IMPORT (utilisé par ImportController, endpoint unique)
    // Supprime le(s) campus existant(s) puis réimporte depuis Campus.json.
    // À appeler seulement APRÈS que les bâtiments référençant l'ancien campus
    // ont été supprimés (contrainte de clé étrangère batiment.campus_id).

    public CampusDTO resetAndImportFromLocalFile() {
        campusRepository.deleteAll();
        log.info("Campus existant(s) supprimé(s), réimport en cours");
        return doImport();
    }

    private CampusDTO doImport() {
        String resourcePath = "data/Campus.json";
        try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode features = root.path("features");

            // On récupère TOUTES les features "Campus de Bordeaux" (plusieurs
            // parties possibles), pas seulement la première.
            List<Geometry> parts = new ArrayList<>();
            for (JsonNode feature : features) {
                String featureName = feature.path("properties").path("name").asText("");
                if (featureName.equalsIgnoreCase(CAMPUS_FEATURE_NAME)) {
                    try {
                        parts.add(GeometryUtils.parseGeometry(feature.path("geometry"), geometryFactory));
                    } catch (Exception ex) {
                        log.warn("Partie de campus ignorée : géométrie invalide ({})", ex.getMessage());
                    }
                }
            }
            if (parts.isEmpty()) {
                throw new RuntimeException(
                        "Aucune feature '" + CAMPUS_FEATURE_NAME + "' valide dans " + resourcePath);
            }

            log.info("{} partie(s) de campus trouvée(s) dans le fichier, fusion en cours", parts.size());
            Geometry geometry = parts.size() == 1 ? parts.get(0) : GeometryUtils.union(parts);

            Point centroid = geometry.getCentroid();

            Campus campus = new Campus();
            campus.setName(CAMPUS_NAME);
            campus.setCity("Bordeaux");
            campus.setCenterLat(centroid.getY());
            campus.setCenterLng(centroid.getX());
            campus.setPolygon(geometry);
            campus.setPerimeterMeters(GeometryUtils.perimeterMeters(geometry));
            campus.setImportedAt(LocalDateTime.now());

            log.info("Sauvegarde campus : {} ({} points, {} partie(s))",
                    CAMPUS_NAME, geometry.getNumPoints(), geometry.getNumGeometries());
            return toDTO(campusRepository.save(campus));

        } catch (Exception e) {
            throw new RuntimeException("Erreur import fichier local Campus.json", e);
        }
    }

    // ─── DTO

    private CampusDTO toDTO(Campus campus) {
        return CampusDTO.builder()
                .id(campus.getId())
                .name(campus.getName())
                .city(campus.getCity())
                .centerLat(campus.getCenterLat())
                .centerLng(campus.getCenterLng())
                .perimeterMeters(campus.getPerimeterMeters())
                .polygonCoordinates(GeometryUtils.extractExteriorRings(campus.getPolygon()))
                .importedAt(campus.getImportedAt())
                .build();
    }

    public CampusDTO updateCampus(Long id, CampusUpdateDTO request) {
    Campus campus = campusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Campus introuvable : " + id));
    if (request.getName() != null) campus.setName(request.getName());
    if (request.getCity() != null) campus.setCity(request.getCity());
    return toDTO(campusRepository.save(campus));
}
}