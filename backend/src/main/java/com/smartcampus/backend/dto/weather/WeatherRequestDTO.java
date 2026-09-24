package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRequestDTO {


    private Double latitude;
    private Double longitude;
    private LocalDate date;
    private LocalTime time;

}