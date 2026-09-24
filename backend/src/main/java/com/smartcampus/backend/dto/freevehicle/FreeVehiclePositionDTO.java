package com.smartcampus.backend.dto.freevehicle;

import lombok.*;
//dto pour la position d'un vehicule libre (bikeId, vehicleTypeId, latitude, longitude)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FreeVehiclePositionDTO {
    private String bikeId;
    private String vehicleTypeId;
    private Double latitude;
    private Double longitude;
}