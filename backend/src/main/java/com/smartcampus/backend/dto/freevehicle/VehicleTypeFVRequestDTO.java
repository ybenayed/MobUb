package com.smartcampus.backend.dto.freevehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleTypeFVRequestDTO {
    private String vehicleTypeId;
    private String formFactor;
    private String propulsionType;
    private String name;
    private Integer maxRangeMeters;
}