package com.smartcampus.backend.dto.freevehicle;

import lombok.*;
//dto pour le nombre de vehicules libres par type de vehicule (vehicleTypeId, name, count)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleTypeCountDTO {
    private String vehicleTypeId;
    private String name;
    private Long count;
}