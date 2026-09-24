package com.smartcampus.backend.entity.station;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "station_v")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String stationId;   

    private String nom;
    private String adresse;
    private Integer capacite;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;
}