package com.smartcampus.backend.dto.parking;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingDynamicDTO {
    private String ident;
    private String etat;
    private Integer libre;      
    private Integer totalTempsReel; 
    private Boolean connecte;  
    private OffsetDateTime mdate;
    private OffsetDateTime fetchedAt;
}