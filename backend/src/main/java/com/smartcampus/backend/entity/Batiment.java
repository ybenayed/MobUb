package com.smartcampus.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@Entity
@Table(name = "batiment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Batiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom du bâtiment (ex: "A1", "Résidence Pierre et Marie Curie"...)
    @Column(name = "nom", nullable = true)
    private String name;

    // Institution / entité d'appartenance (ex: "Université de Bordeaux", "CNRS", "CROUS"...)
    @Column(name = "appartenance")
    private String appartenance;

    @Column(name = "couleur_fill")
    private String fillColor;

    @Column(name = "couleur_stroke")
    private String strokeColor;

    private Double centerLat;
    private Double centerLng;

    private Double perimeterMeters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    // Geometry générique : peut être un Polygon ou un MultiPolygon
    // (bâtiment en plusieurs blocs)
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