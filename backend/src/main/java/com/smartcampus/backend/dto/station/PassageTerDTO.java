package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PassageTerDTO {
    private String ligne;         
    private String modeCommercial; 
    private String direction;      
    private String destination;    
    private String heureTheorique; 
    private String heurePrevue;    
    private Long retardSecondes;   
    private boolean tempsReel;    
}