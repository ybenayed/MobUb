package com.smartcampus.backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatimentDTO {
    private Long id;
    private String name;
    private String appartenance;
    private String fillColor;
    private String strokeColor;
    private Double centerLat;
    private Double centerLng;
    private Double perimeterMeters;
    private Long campusId;
    // Liste de parties (voir CampusDTO) : la plupart des bâtiments ont une seule partie
    private List<List<double[]>> polygonCoordinates;
    private LocalDateTime importedAt;
}