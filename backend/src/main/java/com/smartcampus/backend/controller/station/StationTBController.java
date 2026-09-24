package com.smartcampus.backend.controller.station;

import com.smartcampus.backend.dto.station.PassageTBDTO;
import com.smartcampus.backend.dto.station.StationTBDTO;
import com.smartcampus.backend.dto.station.StationPositionTBDTO;
import com.smartcampus.backend.service.station.PassageTBService;
import com.smartcampus.backend.service.station.StationTBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
// Controller pour la gestion des stations de bus/tram
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stationTB")
@CrossOrigin(origins = "*")
public class StationTBController {

    private final StationTBService stationTBService;   // STATIQUE
    private final PassageTBService passageTBService;    // DYNAMIQUE

    //  Import

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importStations() {
        log.info(">> Import stations bus/tram depuis API Mecatran");
        return ResponseEntity.ok(Map.of("imported", stationTBService.importStationsFromApi()));
    }

    @GetMapping
    public ResponseEntity<List<StationTBDTO>> getAllStationsTB() {
        return ResponseEntity.ok(stationTBService.getAllStations());
    }

    //le get selon le type de transport (bus ou tram)
    @GetMapping("/positions")
    public ResponseEntity<List<StationPositionTBDTO>> getPositions(
            @RequestParam(required = false) String mode) {
        if (mode != null) {
            return ResponseEntity.ok(stationTBService.getPositionsByMode(mode.toUpperCase()));
        }
        return ResponseEntity.ok(stationTBService.getAllPositions());
    }

    // Temps reel (dynamique)

    @GetMapping("/passages")
    public ResponseEntity<List<PassageTBDTO>> getNextPassages(@RequestParam String stopId) {
        return ResponseEntity.ok(passageTBService.getNextPassages(stopId));
    }
}