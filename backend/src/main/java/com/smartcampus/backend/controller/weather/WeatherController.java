package com.smartcampus.backend.controller.weather;

import com.smartcampus.backend.dto.weather.CurrentWeatherDTO;
import com.smartcampus.backend.dto.weather.DailyWeatherDTO;
import com.smartcampus.backend.dto.weather.HourlyPointDTO;
import com.smartcampus.backend.dto.weather.WeatherAtDTO;
import com.smartcampus.backend.dto.weather.WeatherRequestDTO;
import com.smartcampus.backend.dto.weather.WeatherSummaryDTO;
import com.smartcampus.backend.service.weather.WeatherLiveCacheService;
import com.smartcampus.backend.service.weather.WeatherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controller pour la gestion de la météo
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherLiveCacheService weatherLiveCacheService;
    private final WeatherQueryService weatherQueryService;

    // Meteo "campus" en cache (rafraichie toutes les 30 min) ───

    @GetMapping("/summary")
    public ResponseEntity<WeatherSummaryDTO> getSummary() {
        WeatherSummaryDTO summary = weatherLiveCacheService.getSummary();
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.notFound().build();
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentWeatherDTO> getCurrent() {
        CurrentWeatherDTO current = weatherLiveCacheService.getCurrent();
        return current != null ? ResponseEntity.ok(current) : ResponseEntity.notFound().build();
    }

    @GetMapping("/hourly/today")
    public ResponseEntity<List<HourlyPointDTO>> getHourlyToday() {
        return ResponseEntity.ok(weatherLiveCacheService.getHourlyToday());
    }

    // 7 prochains jours (+ aujourd'hui)
    @GetMapping("/forecast")
    public ResponseEntity<List<DailyWeatherDTO>> getForecast() {
        return ResponseEntity.ok(weatherLiveCacheService.getForecastDaily());
    }

    // 7 derniers jours
    @GetMapping("/past")
    public ResponseEntity<List<DailyWeatherDTO>> getPast() {
        return ResponseEntity.ok(weatherLiveCacheService.getPastDaily());
    }

    // Meteo a la demande pour des coordonnees/date/heure arbitraires ───

    @PostMapping("/at")
    public ResponseEntity<?> getWeatherAt(@RequestBody WeatherRequestDTO request) {
        try {
            WeatherAtDTO result = weatherQueryService.getWeatherAt(request);
            return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}