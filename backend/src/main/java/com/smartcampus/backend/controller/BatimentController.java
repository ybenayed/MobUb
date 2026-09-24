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
// Controller pour la gestion des bâtiments et des couleurs d'institutions
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batiments")
@CrossOrigin(origins = "*") 
public class BatimentController {

    private final BatimentService batimentService;
    private final InstitutionColorService institutionColorService;

    @GetMapping
    public ResponseEntity<List<BatimentDTO>> getAllBatiments() {
        return ResponseEntity.ok(batimentService.getAllBatiments());
    }

    @GetMapping("/campus/{campusId}")
    public ResponseEntity<List<BatimentDTO>> getBatimentsByCampus(@PathVariable Long campusId) {
        return ResponseEntity.ok(batimentService.getBatimentsByCampus(campusId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatimentDTO> getBatimentById(@PathVariable Long id) {
        return ResponseEntity.ok(batimentService.getBatimentById(id));
    }

    @GetMapping("/legendes/couleurs")
    public ResponseEntity<List<InstitutionColorDTO>> getInstitutionColors() {
        institutionColorService.importFromLocalFileIfEmpty();
        return ResponseEntity.ok(institutionColorService.getAllAsDto());
    }
}