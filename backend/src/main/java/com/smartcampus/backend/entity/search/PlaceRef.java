package com.smartcampus.backend.entity.search;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Bloc reutilisable pour stocker un point nomme (origine, destination, from/to d'un leg).
 * Embarque directement dans SearchHistory et SearchHistoryLeg via @Embedded
 * + @AttributeOverrides pour prefixer les colonnes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PlaceRef {
    private String name;
    private Double lat;
    private Double lon;
}