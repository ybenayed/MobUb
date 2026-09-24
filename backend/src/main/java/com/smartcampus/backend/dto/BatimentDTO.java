package com.smartcampus.backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
// Partie statique  des batiments 
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
    private List<List<double[]>> polygonCoordinates;
    private LocalDateTime importedAt;
}