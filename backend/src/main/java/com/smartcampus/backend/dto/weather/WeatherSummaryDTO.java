package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherSummaryDTO {
    private Double latitude;
    private Double longitude;
    private CurrentWeatherDTO current;
    private List<HourlyPointDTO> hourlyToday;
    private List<DailyWeatherDTO> pastDaily;
    private List<DailyWeatherDTO> forecastDaily;

}