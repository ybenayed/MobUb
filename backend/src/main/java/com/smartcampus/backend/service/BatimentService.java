package com.smartcampus.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.BatimentDTO;
import com.smartcampus.backend.dto.BatimentUpdateDTO;
import com.smartcampus.backend.entity.Batiment;
import com.smartcampus.backend.entity.Campus;
import com.smartcampus.backend.repository.BatimentRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatimentService {

    private final BatimentRepository batimentRepository;
    private final CampusRepository campusRepository;
    private final InstitutionColorService institutionColorService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<BatimentDTO> getAllBatiments() {
        return batimentRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<BatimentDTO> getBatimentsByCampus(Long campusId) {
        return batimentRepository.findByCampusId(campusId).stream().map(this::toDTO).toList();
    }

    public BatimentDTO getBatimentById(Long id) {
        return batimentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Bâtiment introuvable : " + id));
    }

    /**
     * Supprime tous les bâtiments. Utilisé par ImportController AVANT de
     * supprimer le campus (contrainte de clé étrangère batiment.campus_id),
     * dans le flux d'import unique "tout réimporter".
     */
    public void deleteAll() {
        long count = batimentRepository.count();
        batimentRepository.deleteAll();
        log.info("{} bâtiment(s) supprimé(s)", count);
    }

    // ─── IMPORT DES BÂTIMENTS
    // Suppose que la table est vide (appelée après deleteAll() + réimport du campus
    // dans le flux ImportController). Importe tout depuis Campus.json.

    public List<BatimentDTO> importBatimentsFromLocalFile() {
        Campus campus = campusRepository.findByName("Campus Bordeaux")
                .orElseThrow(() -> new RuntimeException(
                        "Le campus doit être importé avant les bâtiments"));

        Map<String, String> institutionColors = institutionColorService.getColorMap();
        String defaultColor = institutionColorService.getDefaultColor();

        // Table inversée couleur -> institution. Les fichiers GeoJSON encodent
        // l'institution via la couleur "fill" du bâtiment (couleur officielle,
        // identique à celle de institution_color), ce qui est BEAUCOUP plus fiable
        // que de chercher un mot-clé dans le nom : des bâtiments comme "A", "B",
        // "ENSC", "Accueil", "Serre" ne contiennent jamais le nom de l'institution.
        Map<String, String> colorToInstitution = new HashMap<>();
        for (Map.Entry<String, String> e : institutionColors.entrySet()) {
            if (e.getValue() != null) {
                colorToInstitution.put(e.getValue().toUpperCase(), e.getKey());
            }
        }

        String resourcePath = "data/Campus.json";
        try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode features = root.path("features");

            List<Batiment> batiments = new ArrayList<>();
            int totalFeatures = 0;
            List<String> skippedNames = new ArrayList<>();
            List<String> unresolvedColors = new ArrayList<>();

           for (JsonNode feature : features) {
    totalFeatures++;

    String name = feature.path("properties").path("name").asText(null);

    if (name != null && name.equalsIgnoreCase("Campus de Bordeaux")) {
        continue; // C'est le contour du campus, géré par CampusService
    }

    Geometry geometry;
    try {
        geometry = GeometryUtils.parseGeometry(feature.path("geometry"), geometryFactory);
    } catch (Exception ex) {
        log.warn("Bâtiment '{}' ignoré : géométrie invalide ({})", name, ex.getMessage());
        skippedNames.add(name);
        continue;
    }

    Point centroid = geometry.getCentroid();

    String geoFill = feature.path("properties").path("fill").asText(null);
    String geoStroke = feature.path("properties").path("stroke").asText(null);

    String appartenance = resolveAppartenance(feature, name, geoFill, colorToInstitution);

    if (geoFill != null && !colorToInstitution.containsKey(geoFill.toUpperCase())
            && !"Université de Bordeaux".equalsIgnoreCase(appartenance)) {
        unresolvedColors.add((name != null ? name : "Sans Nom") + " (" + geoFill + ")");
    }

    // Sélection de la couleur : priorité à geoFill, sinon couleur de l'appartenance, sinon couleur par défaut
    String fillColor = (geoFill != null) ? geoFill : institutionColors.getOrDefault(appartenance, defaultColor);
    String strokeColor = (geoStroke != null) ? geoStroke : fillColor;

    Batiment batiment = new Batiment();
    batiment.setName(name); 
    batiment.setAppartenance(appartenance);
    batiment.setFillColor(fillColor);
    batiment.setStrokeColor(strokeColor);
    batiment.setCenterLat(centroid.getY());
    batiment.setCenterLng(centroid.getX());
    batiment.setPerimeterMeters(GeometryUtils.perimeterMeters(geometry));
    batiment.setCampus(campus);
    batiment.setPolygon(geometry);

    batiments.add(batiment);
}

            log.info("Import bâtiments : {} features lues, {} bâtiments valides, {} ignoré(s){}",
                    totalFeatures, batiments.size(), skippedNames.size(),
                    skippedNames.isEmpty() ? "" : " -> " + skippedNames);

            if (!unresolvedColors.isEmpty()) {
                log.warn("{} bâtiment(s) avec une couleur GeoJSON inconnue de institution_color "
                                + "(institution probablement manquante en base) : {}",
                        unresolvedColors.size(), unresolvedColors);
            }

            return batimentRepository.saveAll(batiments).stream().map(this::toDTO).toList();

        } catch (Exception e) {
            throw new RuntimeException("Erreur import fichier local Campus.json", e);
        }
    }


private String resolveAppartenance(JsonNode feature, String name, String geoFill,
                                    Map<String, String> colorToInstitution) {
    if (feature.path("properties").has("appartenance")) {
        return feature.path("properties").path("appartenance").asText();
    }
    if (feature.path("properties").has("institution")) {
        return feature.path("properties").path("institution").asText();
    }

    if (geoFill != null) {
        String byColor = colorToInstitution.get(geoFill.toUpperCase());
        if (byColor != null) {
            return byColor;
        }
    }

    if (name != null) {
        String lower = name.toLowerCase();
        if (lower.contains("résidence") || lower.contains("residence") || lower.contains("crous")) {
            return "CROUS";
        }
        if (lower.contains("cnrs")) {
            return "CNRS";
        }
        if (lower.contains("montaigne")) {
            return "Université Bordeaux Montaigne";
        }
        if (lower.contains("inp")) {
            return "Bordeaux INP";
        }
        if (lower.contains("sciences po")) {
            return "Sciences Po Bordeaux";
        }
        if (lower.contains("agro")) {
            return "Bordeaux Sciences Agro";
        }
        if (lower.contains("architecture") || lower.contains("ensap")) {
            return "École Nat. Sup. Architecture et Paysage";
        }
    }

    return "Université de Bordeaux";
}

    private BatimentDTO toDTO(Batiment b) {
        return BatimentDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .appartenance(b.getAppartenance())
                .fillColor(b.getFillColor())
                .strokeColor(b.getStrokeColor())
                .centerLat(b.getCenterLat())
                .centerLng(b.getCenterLng())
                .perimeterMeters(b.getPerimeterMeters())
                .campusId(b.getCampus() != null ? b.getCampus().getId() : null)
                .polygonCoordinates(GeometryUtils.extractExteriorRings(b.getPolygon()))
                .importedAt(b.getImportedAt())
                .build();
    }



    public BatimentDTO updateBatiment(Long id, BatimentUpdateDTO request) {
        Batiment b = batimentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bâtiment introuvable : " + id));
        if (request.getName() != null) b.setName(request.getName());
        if (request.getAppartenance() != null) b.setAppartenance(request.getAppartenance());
        if (request.getFillColor() != null) b.setFillColor(request.getFillColor());
        if (request.getStrokeColor() != null) b.setStrokeColor(request.getStrokeColor());
        return toDTO(batimentRepository.save(b));
    }

    public void deleteBatiment(Long id) {
        if (!batimentRepository.existsById(id)) {
            throw new RuntimeException("Bâtiment introuvable : " + id);
        }
        batimentRepository.deleteById(id);
    }
}