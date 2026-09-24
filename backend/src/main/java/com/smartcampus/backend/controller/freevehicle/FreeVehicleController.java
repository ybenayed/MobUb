package com.smartcampus.backend.controller.freevehicle;

import com.smartcampus.backend.dto.freevehicle.FreeVehicleDTO;
import com.smartcampus.backend.dto.freevehicle.FreeVehiclePositionDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeCountDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVDTO;
import com.smartcampus.backend.service.freevehicle.FreeVehicleInfoService;
import com.smartcampus.backend.service.freevehicle.FreeVehiclePositionCacheService;
import com.smartcampus.backend.service.freevehicle.VehicleTypeFVService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
// Controller pour la gestion des vehicules libres (scooters, velos, trottinettes) 
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/freeVehicle")
@CrossOrigin(origins = "*")
public class FreeVehicleController {

    private final VehicleTypeFVService vehicleTypeFVService;                     
    private final FreeVehicleInfoService freeVehicleInfoService;                  // 100% dynamique (detail, liste, comptage)
    private final FreeVehiclePositionCacheService freeVehiclePositionCacheService; // positions brutes (cache 10s)

    @PostMapping("/import-types")
    public ResponseEntity<Map<String, Integer>> importVehicleTypes() {
        log.info(">> Import types de vehicules depuis API RideYeGo");
        return ResponseEntity.ok(Map.of("imported", vehicleTypeFVService.importTypesFromApi()));
    }

    //  API Front - 100% dynamique desormais

    @GetMapping("/types")
    public ResponseEntity<List<VehicleTypeFVDTO>> getAllTypes() {
        return ResponseEntity.ok(vehicleTypeFVService.getAllTypes());
    }

    @GetMapping
    public ResponseEntity<List<FreeVehicleDTO>> getAllVehicles(
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(freeVehicleInfoService.getVehiclesByType(type));
        }
        return ResponseEntity.ok(freeVehicleInfoService.getAllVehicles());
    }

    // Temps reel (positions seules, format allege pour la carte)

    @GetMapping("/positions")
    public ResponseEntity<List<FreeVehiclePositionDTO>> getPositions(
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(freeVehiclePositionCacheService.getPositionsByType(type));
        }
        return ResponseEntity.ok(freeVehiclePositionCacheService.getAllPositions());
    }

    //  Detail complet d'un vehicule - 100% dynamique

    @GetMapping("/{bikeId}")
    public ResponseEntity<FreeVehicleDTO> getVehicleInfo(@PathVariable String bikeId) {
        FreeVehicleDTO dto = freeVehicleInfoService.getVehicleInfo(bikeId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // Comptage par type 

    @GetMapping("/types/count")
    public ResponseEntity<List<VehicleTypeCountDTO>> getVehicleCountByType() {
        return ResponseEntity.ok(freeVehicleInfoService.getVehicleCountByType());
    }
}