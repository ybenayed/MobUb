package com.smartcampus.backend.dto.search;

import java.util.List;
import lombok.*;
@Getter
@Setter
public class ItineraryRequestDTO {

    private SearchResultDTO origin;
    private SearchResultDTO destination;

    private Integer numItineraries;      // ex: 3, 5, 10...
    private List<String> modes;          // ex: ["WALK","BICYCLE_RENT","TRANSIT"]
    private String date;                 // format "yyyy-MM-dd", ex: "2026-08-26"
    private String time;                 // format "HH:mm", ex: "14:30"
    private Boolean arriveBy;            // true = "je veux arriver a" / false = "je pars a"
    private Double walkSpeed;            // m/s, ex: 1.3
    private Double bikeSpeed;            // m/s, ex: 4.0
    private Boolean wheelchair;          // accessibilite PMR

    public ItineraryRequestDTO() {
    }

}