package com.smartcampus.backend.controller.weather;

import com.smartcampus.backend.dto.weather.AirQualityAtDTO;
import com.smartcampus.backend.dto.weather.AirQualitySummaryDTO;
import com.smartcampus.backend.dto.weather.CurrentAirQualityDTO;
import com.smartcampus.backend.dto.weather.CurrentWeatherDTO;
import com.smartcampus.backend.dto.weather.DailyAirQualityDTO;
import com.smartcampus.backend.dto.weather.DailyWeatherDTO;
import com.smartcampus.backend.dto.weather.HourlyAirQualityPointDTO;
import com.smartcampus.backend.dto.weather.HourlyPointDTO;
import com.smartcampus.backend.dto.weather.WeatherAtDTO;
import com.smartcampus.backend.dto.weather.WeatherRequestDTO;
import com.smartcampus.backend.dto.weather.WeatherSummaryDTO;
import com.smartcampus.backend.service.weather.AirQualityLiveCacheService;
import com.smartcampus.backend.service.weather.AirQualityQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controller pour la gestion de la qualité de l'air
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air-quality")
@CrossOrigin(origins = "*")
public class AirQualityController {


    private final AirQualityLiveCacheService airQualityLiveCacheService;
    private final AirQualityQueryService airQualityQueryService;


    @GetMapping("/summary")
    public ResponseEntity<AirQualitySummaryDTO> getAirQualitySummary() {
        AirQualitySummaryDTO summary = airQualityLiveCacheService.getSummary();
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.notFound().build();
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentAirQualityDTO> getAirQualityCurrent() {
        CurrentAirQualityDTO current = airQualityLiveCacheService.getCurrent();
        return current != null ? ResponseEntity.ok(current) : ResponseEntity.notFound().build();
    }

    @GetMapping("/hourly/today")
    public ResponseEntity<List<HourlyAirQualityPointDTO>> getAirQualityHourlyToday() {
        return ResponseEntity.ok(airQualityLiveCacheService.getHourlyToday());
    }

    // 7 prochains jours (+ aujourd'hui)
    @GetMapping("/forecast")
    public ResponseEntity<List<DailyAirQualityDTO>> getAirQualityForecast() {
        return ResponseEntity.ok(airQualityLiveCacheService.getForecastDaily());
    }

    // 7 derniers jours
    @GetMapping("/past")
    public ResponseEntity<List<DailyAirQualityDTO>> getAirQualityPast() {
        return ResponseEntity.ok(airQualityLiveCacheService.getPastDaily());
    }

    // Qualite de l'air a la demande pour des coordonnees/date/heure arbitraires
    @PostMapping("/at")
    public ResponseEntity<?> getAirQualityAt(@RequestBody WeatherRequestDTO request) {
        try {
            AirQualityAtDTO result = airQualityQueryService.getAirQualityAt(request);
            return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}