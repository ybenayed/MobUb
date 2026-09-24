package com.smartcampus.backend.entity.station;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Entity
@Table(name = "station_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationTB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String stopId;      // ex: bordeaux:StopPoint:BP:3729:LOC

    private String nom;
    private String stopAreaRef; 
    private String mode;        // BUS / TRAM / INCONNU 

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @ElementCollection
    @CollectionTable(name = "station_lines", joinColumns = @JoinColumn(name = "station_id"))
    @Column(name = "line_ref")
    private List<String> lines;
}