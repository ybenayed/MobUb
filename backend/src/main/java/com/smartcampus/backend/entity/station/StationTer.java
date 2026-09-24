package com.smartcampus.backend.entity.station;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "station_ter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationTer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String navitiaId;   // ex: stop_area:SNCF:87581009

    private String nom;      

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private Double distanceCentreMetres; 
}