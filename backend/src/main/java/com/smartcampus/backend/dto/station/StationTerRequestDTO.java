package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationTerRequestDTO {
    private String navitiaId;
    private String nom;
    private Double latitude;
    private Double longitude;
}