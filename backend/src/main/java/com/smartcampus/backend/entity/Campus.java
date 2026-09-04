package com.smartcampus.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@Entity
@Table(name = "campus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String name;
    private String city;

    private Double centerLat;
    private Double centerLng;

    private Double perimeterMeters;

    // Geometry générique : peut être un Polygon ou un MultiPolygon
    // (ex : campus en plusieurs parties disjointes)
    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry polygon;

    private LocalDateTime importedAt;

    @PrePersist
    public void prePersist() {
        if (importedAt == null) {
            importedAt = LocalDateTime.now();
        }
    }
}