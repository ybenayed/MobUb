package com.smartcampus.backend.dto.search.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryLegResponseDTO {
    private String mode;
    private String fromName;
    private double fromLat;
    private double fromLon;
    private String toName;
    private double toLat;
    private double toLon;
    private Double distance;
    private Boolean rentedBike;
    private String routeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}