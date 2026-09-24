package com.smartcampus.backend.dto.freevehicle;

import lombok.*;
//dto pour la creation d'un type de vehicule libre (vehicleTypeId, formFactor, propulsionType, name, maxRangeMeters)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleTypeFVRequestDTO {
    private String vehicleTypeId;
    private String formFactor;
    private String propulsionType;
    private String name;
    private Integer maxRangeMeters;
}