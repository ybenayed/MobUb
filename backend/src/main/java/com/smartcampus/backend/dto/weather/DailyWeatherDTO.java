package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyWeatherDTO {
    private LocalDate date;
    private Double temperatureMax;
    private Double temperatureMin;
    private Integer weathercode;
    private String description; 
    private String icon;       
    private Double precipitationSum;
    private Double windspeedMax;
}