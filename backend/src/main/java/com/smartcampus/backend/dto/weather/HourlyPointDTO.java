package com.smartcampus.backend.dto.weather;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HourlyPointDTO {
    private String time;
    private Double temperature;
    private Integer weathercode;
    private String description; 
    private String icon;        
    private Integer precipitationProbability;
}