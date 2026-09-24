package com.smartcampus.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
// Partie statique  des campus
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusDTO {

    private Long id;
    private String name;
    private String city;
    private Double centerLat;
    private Double centerLng;
    private Double perimeterMeters;
    private List<List<double[]>> polygonCoordinates;

    private LocalDateTime importedAt;
}