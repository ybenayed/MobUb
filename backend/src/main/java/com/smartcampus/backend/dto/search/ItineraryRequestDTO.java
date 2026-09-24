package com.smartcampus.backend.dto.search;

import java.util.List;
import lombok.*;

@Getter
@Setter
public class ItineraryRequestDTO {

    private SearchResultDTO origin;
    private SearchResultDTO destination;

    private List<String> modes;          
    private String date;                
    private String time;                 
    private Boolean arriveBy;            
    private Double walkSpeed;            
    private Double bikeSpeed;            
    private Boolean wheelchair;         

    public ItineraryRequestDTO() {
    }

}