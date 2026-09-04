// LegDTO.java
package com.smartcampus.backend.dto.search;

import lombok.*;
import java.util.List;

/**
 * Un segment d'itineraire : marche, tram, bus, TER...
 * startTime/endTime : timestamps epoch millisecondes, calcules cote backend
 * a partir de departure.scheduledTime / arrival.scheduledTime (OTP renvoie
 * desormais des OffsetDateTime ISO-8601, pas des epoch millis directs).
 * rentedBike : true si ce segment est fait avec un velo en libre-service (Vcub),
 * permet au frontend de distinguer visuellement "velo perso" et "Vcub" alors
 * qu'OTP renvoie le meme mode "BICYCLE" pour les deux.
 */
@Getter
@Setter
public class LegDTO {
    private String mode;
    private String fromName;
    private String toName;
    private long startTime;
    private long endTime;
    private double distance;
    private boolean rentedBike;
    private String routeName;
    private List<GeoPointDTO> geometry;

    public LegDTO() {}

    public LegDTO(String mode, String fromName, String toName,
                  long startTime, long endTime, double distance, boolean rentedBike,
                  String routeName, List<GeoPointDTO> geometry) {
        this.mode = mode;
        this.fromName = fromName;
        this.toName = toName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.rentedBike = rentedBike;
        this.routeName = routeName;
        this.geometry = geometry;
    }
}