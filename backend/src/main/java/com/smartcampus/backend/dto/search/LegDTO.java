// LegDTO.java
package com.smartcampus.backend.dto.search;

/**
 * Un segment d'itineraire : marche, tram, bus, TER...
 * startTime/endTime : timestamps epoch millisecondes (format brut OTP),
 * a convertir en heure locale cote Android pour l'affichage.
 */
// LegDTO.java
import lombok.*;
import java.util.List;

@Getter
@Setter
public class LegDTO {
    private String mode;
    private String fromName;
    private String toName;
    private long startTime;
    private long endTime;
    private String routeName;
    private List<GeoPointDTO> geometry; // <-- le tracé réel du leg (suit les rails/routes)

    public LegDTO() {}

    public LegDTO(String mode, String fromName, String toName,
                  long startTime, long endTime, String routeName,
                  List<GeoPointDTO> geometry) {
        this.mode = mode;
        this.fromName = fromName;
        this.toName = toName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.routeName = routeName;
        this.geometry = geometry;
    }
}