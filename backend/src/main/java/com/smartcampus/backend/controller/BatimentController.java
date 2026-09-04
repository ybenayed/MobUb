package com.smartcampus.backend.controller;

import com.smartcampus.backend.dto.BatimentDTO;
import com.smartcampus.backend.dto.InstitutionColorDTO;
import com.smartcampus.backend.service.BatimentService;
import com.smartcampus.backend.service.InstitutionColorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batiments")
@CrossOrigin(origins = "*") // à restreindre en prod
public class BatimentController {

    private final BatimentService batimentService;
    private final InstitutionColorService institutionColorService;

    // L'import se fait maintenant uniquement via POST /api/import/local (ImportController),
    // qui gère campus + bâtiments + couleurs ensemble dans le bon ordre.

    /** GET /api/batiments — tous les bâtiments, tous campus confondus. */
    @GetMapping
    public ResponseEntity<List<BatimentDTO>> getAllBatiments() {
        return ResponseEntity.ok(batimentService.getAllBatiments());
    }

    /** GET /api/batiments/campus/{campusId} */
    @GetMapping("/campus/{campusId}")
    public ResponseEntity<List<BatimentDTO>> getBatimentsByCampus(@PathVariable Long campusId) {
        return ResponseEntity.ok(batimentService.getBatimentsByCampus(campusId));
    }

    /** GET /api/batiments/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<BatimentDTO> getBatimentById(@PathVariable Long id) {
        return ResponseEntity.ok(batimentService.getBatimentById(id));
    }

    /**
     * GET /api/batiments/legendes/couleurs
     * Table institution -> couleur, lue depuis la base.
     */
    @GetMapping("/legendes/couleurs")
    public ResponseEntity<List<InstitutionColorDTO>> getInstitutionColors() {
        institutionColorService.importFromLocalFileIfEmpty();
        return ResponseEntity.ok(institutionColorService.getAllAsDto());
    }
}