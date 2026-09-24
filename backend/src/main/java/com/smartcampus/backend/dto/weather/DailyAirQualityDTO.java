package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyAirQualityDTO {
    private LocalDate date;
    private Double pm2_5Avg;
    private Double pm10Avg;
    private Double ozoneAvg;
    private Double nitrogenDioxideAvg;
    private Integer europeanAqiMax; // pire valeur de la journee ( sert a la conversion icone/signification)
    private String category;
    private String description;
    private String icon;
}