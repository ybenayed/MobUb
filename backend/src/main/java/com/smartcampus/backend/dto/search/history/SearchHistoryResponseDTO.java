package com.smartcampus.backend.dto.search.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponseDTO {
    private Long id;
    private String originName;
    private String destinationName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private long duration;
    private int numberOfTransfers;
    private Double walkDistance;
    private Double accessibilityScore;
    private Double co2Grams;
    private String profileLabel;
    private LocalDateTime searchedAt;
    private List<SearchHistoryLegResponseDTO> legs;
}