package com.smartcampus.backend.dto;

import lombok.*;
// Partie statique  des batiments
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BatimentUpdateDTO {
    private String name;
    private String appartenance;
    private String fillColor;
    private String strokeColor;
}