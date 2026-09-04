package com.smartcampus.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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

    // GeoJSON-style : liste de PARTIES, chaque partie étant une liste de [lng, lat].
    // Un campus en une seule pièce a une seule partie ; un campus en plusieurs blocs
    // disjoints (MultiPolygon) a une entrée par bloc, pour que le front les dessine
    // séparément au lieu de tracer une ligne parasite entre les deux.
    private List<List<double[]>> polygonCoordinates;

    private LocalDateTime importedAt;
}