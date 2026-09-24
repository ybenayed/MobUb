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

    @Column(name = "nom", nullable = true)
    private String name;

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