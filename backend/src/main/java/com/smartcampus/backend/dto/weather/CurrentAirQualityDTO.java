package com.smartcampus.backend.dto.weather;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CurrentAirQualityDTO {
    private String time;
    private Double pm2_5;
    private Double pm10;
    private Double ozone;
    private Double nitrogenDioxide;
    private Integer europeanAqi;
    private String category;    // deduit du europeanAqi
    private String description; 
    private String icon;        
}