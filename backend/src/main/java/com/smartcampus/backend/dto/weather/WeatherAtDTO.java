package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WeatherAtDTO {

    private Double latitude;
    private Double longitude;
    private LocalDate date;
    private LocalTime time;

    // Resume du jour demande
    private Double temperatureMax;
    private Double temperatureMin;
    private Double precipitationSum;
    private Double windspeedMax;

    // Point horaire demandé
    private Double temperature;
    private Integer precipitationProbability;

    // Code meteo + traduction (icone / signification)
    private Integer weathercode;
    private String description;
    private String icon;
}