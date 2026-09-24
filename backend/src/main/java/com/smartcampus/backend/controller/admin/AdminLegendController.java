package com.smartcampus.backend.controller.admin;

import com.smartcampus.backend.dto.InstitutionColorDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVRequestDTO;
import com.smartcampus.backend.service.InstitutionColorService;
import com.smartcampus.backend.service.freevehicle.VehicleTypeFVService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controller pour la gestion des legendes (couleurs des institutions et types de vehicules) par l'admin
@RestController
@RequestMapping("/api/admin/legends")
@RequiredArgsConstructor
public class AdminLegendController {

    private final InstitutionColorService institutionColorService;
    private final VehicleTypeFVService vehicleTypeFVService;

    @GetMapping("/colors")
    public ResponseEntity<List<InstitutionColorDTO>> getColors() {
        return ResponseEntity.ok(institutionColorService.getAllAsDto());
    }

    @PostMapping("/colors")
    public ResponseEntity<InstitutionColorDTO> createOrUpdateColor(@RequestBody InstitutionColorDTO r) {
        return ResponseEntity.ok(institutionColorService.createOrUpdate(r));
    }

    @DeleteMapping("/colors/{institution}")
    public ResponseEntity<Void> deleteColor(@PathVariable String institution) {
        institutionColorService.delete(institution);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vehicle-types")
    public ResponseEntity<VehicleTypeFVDTO> createVehicleType(@RequestBody VehicleTypeFVRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleTypeFVService.createType(r));
    }

    @PutMapping("/vehicle-types/{id}")
    public ResponseEntity<VehicleTypeFVDTO> updateVehicleType(@PathVariable Long id, @RequestBody VehicleTypeFVRequestDTO r) {
        return ResponseEntity.ok(vehicleTypeFVService.updateType(id, r));
    }

    @DeleteMapping("/vehicle-types/{id}")
    public ResponseEntity<Void> deleteVehicleType(@PathVariable Long id) {
        vehicleTypeFVService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}