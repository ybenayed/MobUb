package com.smartcampus.backend.dto.parking;

import lombok.*;
//dto pour le nombre de parkings par type de parking (taType, count)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingCountDTO {
    private String taType;
    private Long count;
}