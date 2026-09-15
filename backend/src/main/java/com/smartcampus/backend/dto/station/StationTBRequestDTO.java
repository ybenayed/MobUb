package com.smartcampus.backend.dto.station;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationTBRequestDTO {
    private String stopId;
    private String nom;
    private String stopAreaRef;
    private String mode; // optionnel : deduit du stopAreaRef si absent
    private Double latitude;
    private Double longitude;
    private List<String> lines;
}