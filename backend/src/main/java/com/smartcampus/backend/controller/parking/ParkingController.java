package com.smartcampus.backend.controller.parking;

import com.smartcampus.backend.dto.parking.ParkingDTO;
import com.smartcampus.backend.dto.parking.ParkingCountDTO;
import com.smartcampus.backend.dto.parking.ParkingPositionDTO;  
import com.smartcampus.backend.service.parking.ParkingService;
import com.smartcampus.backend.dto.parking.ParkingStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
// Controller pour la gestion des parkings 
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parking")
@CrossOrigin(origins = "*")
public class ParkingController {

    private final ParkingService parkingService;

     //Importe les parkings depuis l'API Bordeaux Metropole. Idempotent.
    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importParking() {
        log.info(">> Import parking depuis API Bordeaux Metropole");
        int imported = parkingService.importParkingFromApi();
        return ResponseEntity.ok(Map.of("imported", imported));
    }

    @GetMapping
    public ResponseEntity<List<ParkingDTO>> getAllParking() {
        return ResponseEntity.ok(parkingService.getAllParking());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingService.getParkingById(id));
    }

    @GetMapping("/positions")
    public ResponseEntity<List<ParkingPositionDTO>> getPositions(
            @RequestParam(required = false) String taType) {
        if (taType != null) {
            return ResponseEntity.ok(parkingService.getPositionsByType(taType));
        }
        return ResponseEntity.ok(parkingService.getAllPositions());
    }

    @GetMapping("/count-by-type")
    public ResponseEntity<List<ParkingCountDTO>> getCountByType() {
        return ResponseEntity.ok(parkingService.getCountByType());
    }
    //la partie  dynamique
    @GetMapping("/status")
    public ResponseEntity<List<ParkingStatusDTO>> getAllWithStatus() {
        return ResponseEntity.ok(parkingService.getAllWithStatus());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ParkingStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingService.getStatusById(id));
    }
}