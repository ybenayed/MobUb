package com.smartcampus.backend.controller;

import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.service.CampusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controller pour la gestion des campus
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campus")
@CrossOrigin(origins = "*") 
public class CampusController {

    private final CampusService campusService;

    @GetMapping
    public ResponseEntity<List<CampusDTO>> getAllCampus() {
        return ResponseEntity.ok(campusService.getAllCampus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampusDTO> getCampusById(@PathVariable Long id) {
        return ResponseEntity.ok(campusService.getCampusById(id));
    }
}