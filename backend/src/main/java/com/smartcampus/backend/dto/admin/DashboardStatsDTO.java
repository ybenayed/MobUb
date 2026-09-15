package com.smartcampus.backend.dto.admin;

import lombok.*;
import java.util.List;

@Getter 
@Setter
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalSearches;
    private long activeUsersLast30Days;
    private double totalCo2GramsSaved;
    private List<ModeCountDTO> mostUsedModes;
}