package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationVRequestDTO {
    private String stationId;
    private String nom;
    private String adresse;
    private Integer capacite;
    private Double latitude;
    private Double longitude;
}