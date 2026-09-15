package com.smartcampus.backend.controller.admin;

import com.smartcampus.backend.dto.station.*;
import com.smartcampus.backend.service.station.StationVService;
import com.smartcampus.backend.service.station.StationTerService;
import com.smartcampus.backend.service.station.StationTBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {

    private final StationVService stationVService;
    private final StationTerService stationTerService;
    private final StationTBService stationTBService;

    @PostMapping("/v")
    public ResponseEntity<StationVDTO> createV(@RequestBody StationVRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationVService.createStation(r));
    }

    @PutMapping("/v/{id}")
    public ResponseEntity<StationVDTO> updateV(@PathVariable Long id, @RequestBody StationVRequestDTO r) {
        return ResponseEntity.ok(stationVService.updateStation(id, r));
    }

    @DeleteMapping("/v/{id}")
    public ResponseEntity<Void> deleteV(@PathVariable Long id) {
        stationVService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ter")
    public ResponseEntity<StationTerDTO> createTer(@RequestBody StationTerRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationTerService.createStation(r));
    }

    @PutMapping("/ter/{id}")
    public ResponseEntity<StationTerDTO> updateTer(@PathVariable Long id, @RequestBody StationTerRequestDTO r) {
        return ResponseEntity.ok(stationTerService.updateStation(id, r));
    }

    @DeleteMapping("/ter/{id}")
    public ResponseEntity<Void> deleteTer(@PathVariable Long id) {
        stationTerService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tb")
    public ResponseEntity<StationTBDTO> createTB(@RequestBody StationTBRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationTBService.createStation(r));
    }

    @PutMapping("/tb/{id}")
    public ResponseEntity<StationTBDTO> updateTB(@PathVariable Long id, @RequestBody StationTBRequestDTO r) {
        return ResponseEntity.ok(stationTBService.updateStation(id, r));
    }

    @DeleteMapping("/tb/{id}")
    public ResponseEntity<Void> deleteTB(@PathVariable Long id) {
        stationTBService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}