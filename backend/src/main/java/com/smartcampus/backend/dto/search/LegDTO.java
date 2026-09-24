package com.smartcampus.backend.dto.search;

import lombok.*;
import java.util.List;
//  dto pour un "leg" d'un itineraire, c'est a dire un segment de trajet entre deux points, avec un mode de transport et des informations sur le trajet.
@Getter
@Setter
public class LegDTO {
    private String mode;
    private String fromName;
    private Double fromLat;
    private Double fromLon;
    private String toName;
    private Double toLat;
    private Double toLon;
    private Long startTime;
    private Long endTime;
    private Double distance;
    private Boolean rentedBike;
    private String routeName;
    private List<GeoPointDTO> geometry;

    public LegDTO() {}

    public LegDTO(String mode, String fromName, double fromLat, double fromLon,
                  String toName, double toLat, double toLon,
                  long startTime, long endTime, double distance, boolean rentedBike,
                  String routeName, List<GeoPointDTO> geometry) {
        this.mode = mode;
        this.fromName = fromName;
        this.fromLat = fromLat;
        this.fromLon = fromLon;
        this.toName = toName;
        this.toLat = toLat;
        this.toLon = toLon;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.rentedBike = rentedBike;
        this.routeName = routeName;
        this.geometry = geometry;
    }
}