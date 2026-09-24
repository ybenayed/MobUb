package com.smartcampus.backend.controller;

import com.smartcampus.backend.dto.BatimentDTO;
import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.service.BatimentService;
import com.smartcampus.backend.service.CampusService;
import com.smartcampus.backend.service.InstitutionColorService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoint UNIQUE d'import : (bâtiments, campus, couleurs) 
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import")
@CrossOrigin(origins = "*") 
public class ImportController {

    private final CampusService campusService;
    private final BatimentService batimentService;
    private final InstitutionColorService institutionColorService;

    /**
     * Ordre obligatoire :
     * 1. importe les couleurs d'institution
     * 2. importe les bâtiments (en utilisant le campus + les couleurs)
     */
    @PostMapping("/local")
    @Transactional
    public ResponseEntity<ImportResultDTO> importAll() {
        log.info(">>>>> IMPORT COMPLET (campus + bâtiments + couleurs) — reset puis réimport");

        batimentService.deleteAll();
        CampusDTO campus = campusService.resetAndImportFromLocalFile();
        institutionColorService.resetAndImportFromLocalFile();
        List<BatimentDTO> batiments = batimentService.importBatimentsFromLocalFile();

        log.info(">>>>> IMPORT COMPLET terminé : 1 campus ({} partie(s)), {} bâtiment(s)",
                campus.getPolygonCoordinates().size(), batiments.size());

        return ResponseEntity.ok(ImportResultDTO.builder()
                .campus(campus)
                .batiments(batiments)
                .build());
    }

    @Getter
    @Builder
    public static class ImportResultDTO {
        private CampusDTO campus;
        private List<BatimentDTO> batiments;
    }
}