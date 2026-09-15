package com.smartcampus.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.InstitutionColorDTO;
import com.smartcampus.backend.entity.InstitutionColor;
import com.smartcampus.backend.repository.InstitutionColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Les couleurs par institution vivent en base (table institution_color),
 * peuplée depuis institution_colors.json.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionColorService {

    private final InstitutionColorRepository institutionColorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_INSTITUTION = "CampusBordeaux";
    private static final String DEFAULT_COLOR = "#EAF0D8";
    private static final String RESOURCE_PATH = "data/institution_colors.json";

    /** Importe institution_colors.json vers la base, une seule fois si la table est vide. */
    public void importFromLocalFileIfEmpty() {
        if (institutionColorRepository.count() > 0) {
            return;
        }
        doImport();
    }

    /**
     * Vide la table puis réimporte depuis institution_colors.json.
     * Utilisé par ImportController (endpoint unique "tout réimporter").
     */
    public void resetAndImportFromLocalFile() {
        long count = institutionColorRepository.count();
        institutionColorRepository.deleteAll();
        log.info("{} couleur(s) d'institution supprimée(s), réimport en cours", count);
        doImport();
    }

    private void doImport() {
        try (InputStream is = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            Map<String, String> raw = objectMapper.readValue(is, new TypeReference<Map<String, String>>() {});
            List<InstitutionColor> entities = raw.entrySet().stream()
                    .map(e -> InstitutionColor.builder().institution(e.getKey()).color(e.getValue()).build())
                    .toList();
            institutionColorRepository.saveAll(entities);
            log.info("Import de {} couleurs d'institution depuis {}", entities.size(), RESOURCE_PATH);
        } catch (Exception e) {
            log.error("Impossible d'importer {}", RESOURCE_PATH, e);
        }
    }

    /** Table institution -> couleur, utilisée par BatimentService pendant l'import des bâtiments. */
    public Map<String, String> getColorMap() {
        return institutionColorRepository.findAll().stream()
                .collect(Collectors.toMap(InstitutionColor::getInstitution, InstitutionColor::getColor));
    }

    public String getDefaultColor() {
        return institutionColorRepository.findByInstitution(DEFAULT_INSTITUTION)
                .map(InstitutionColor::getColor)
                .orElse(DEFAULT_COLOR);
    }

    public List<InstitutionColorDTO> getAllAsDto() {
        return institutionColorRepository.findAll().stream()
                .map(c -> new InstitutionColorDTO(c.getInstitution(), c.getColor()))
                .toList();
    }

    public InstitutionColorDTO createOrUpdate(InstitutionColorDTO dto) {
        InstitutionColor entity = institutionColorRepository.findByInstitution(dto.getInstitution())
                .orElse(InstitutionColor.builder().institution(dto.getInstitution()).build());
        entity.setColor(dto.getColor());
        InstitutionColor saved = institutionColorRepository.save(entity);
        return new InstitutionColorDTO(saved.getInstitution(), saved.getColor());
    }

    public void delete(String institution) {
        InstitutionColor entity = institutionColorRepository.findByInstitution(institution)
                .orElseThrow(() -> new RuntimeException("Couleur introuvable pour : " + institution));
        institutionColorRepository.delete(entity);
    }
}