package com.smartcampus.backend.controller.auth;


import com.smartcampus.backend.service.auth.NationalityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expose la liste des nationalités utilisée par le formulaire d'inscription
 * et la page "Mon compte" côté mobile.
 *
 * GET /api/nationalities -> ["Afghane", "Albanaise", ...]
 */
@RestController
@RequestMapping("/api/nationalities")
public class NationalityController {

    private final NationalityService nationalityService;

    public NationalityController(NationalityService nationalityService) {
        this.nationalityService = nationalityService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getNationalities() {
        return ResponseEntity.ok(nationalityService.getAllNationalities());
    }
}