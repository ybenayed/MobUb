package com.smartcampus.backend.dto.station;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PassageTBDTO {
    private String ligne;
    private String direction;
    private String destination;
    private String heureTheorique; 
    private String heurePrevue;    
    private Long retardSecondes;   
}