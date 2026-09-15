package com.smartcampus.backend.controller.admin;

import com.smartcampus.backend.dto.BatimentDTO;
import com.smartcampus.backend.dto.BatimentUpdateDTO;
import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.dto.CampusUpdateDTO;
import com.smartcampus.backend.service.BatimentService;
import com.smartcampus.backend.service.CampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminInfrastructureController {

    private final CampusService campusService;
    private final BatimentService batimentService;

    @PutMapping("/campus/{id}")
    public ResponseEntity<CampusDTO> updateCampus(@PathVariable Long id, @RequestBody CampusUpdateDTO r) {
        return ResponseEntity.ok(campusService.updateCampus(id, r));
    }

    @PutMapping("/batiments/{id}")
    public ResponseEntity<BatimentDTO> updateBatiment(@PathVariable Long id, @RequestBody BatimentUpdateDTO r) {
        return ResponseEntity.ok(batimentService.updateBatiment(id, r));
    }

    @DeleteMapping("/batiments/{id}")
    public ResponseEntity<Void> deleteBatiment(@PathVariable Long id) {
        batimentService.deleteBatiment(id);
        return ResponseEntity.noContent().build();
    }
}