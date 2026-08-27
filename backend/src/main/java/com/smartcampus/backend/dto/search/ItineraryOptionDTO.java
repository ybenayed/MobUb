// ItineraryOptionDTO.java
package com.smartcampus.backend.dto.search;

import java.util.List;

/**
 * Une option d'itineraire proposee par OTP.
 * duration : duree totale du trajet en secondes.
 */
public class ItineraryOptionDTO {
    private long duration;
    private List<LegDTO> legs;

    public ItineraryOptionDTO() {
    }

    public ItineraryOptionDTO(long duration, List<LegDTO> legs) {
        this.duration = duration;
        this.legs = legs;
    }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public List<LegDTO> getLegs() { return legs; }
    public void setLegs(List<LegDTO> legs) { this.legs = legs; }
}