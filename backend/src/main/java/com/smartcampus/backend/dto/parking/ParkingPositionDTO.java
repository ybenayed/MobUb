package com.smartcampus.backend.dto.parking;

import lombok.*;
//dto pour la position d'un parking (id, ident, nom, taType, latitude, longitude)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingPositionDTO {
    private Long id;
    private String ident;
    private String nom;
    private String taType;
    private Double latitude;
    private Double longitude;
}