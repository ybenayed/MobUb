package com.smartcampus.backend.controller;

import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.service.CampusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campus")
@CrossOrigin(origins = "*") // à restreindre en prod
public class CampusController {

    private final CampusService campusService;

    // L'import se fait maintenant uniquement via POST /api/import/local (ImportController),
    // qui gère campus + bâtiments + couleurs ensemble dans le bon ordre.

    /** GET /api/campus — tous les campus enregistrés. */
    @GetMapping
    public ResponseEntity<List<CampusDTO>> getAllCampus() {
        return ResponseEntity.ok(campusService.getAllCampus());
    }

    /** GET /api/campus/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<CampusDTO> getCampusById(@PathVariable Long id) {
        return ResponseEntity.ok(campusService.getCampusById(id));
    }
}