package com.smartcampus.backend.controller.admin;

import com.smartcampus.backend.dto.parking.ParkingDTO;
import com.smartcampus.backend.dto.parking.ParkingRequestDTO;
import com.smartcampus.backend.service.parking.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Controller pour la gestion des parkings par l'admin
@RestController
@RequestMapping("/api/admin/parking")
@RequiredArgsConstructor
public class AdminParkingController {

    private final ParkingService parkingService;

    @PostMapping
    public ResponseEntity<ParkingDTO> create(@RequestBody ParkingRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingService.createParking(r));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingDTO> update(@PathVariable Long id, @RequestBody ParkingRequestDTO r) {
        return ResponseEntity.ok(parkingService.updateParking(id, r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingService.deleteParking(id);
        return ResponseEntity.noContent().build();
    }
}