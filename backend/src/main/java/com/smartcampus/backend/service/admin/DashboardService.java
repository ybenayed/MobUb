package com.smartcampus.backend.service.admin;

import com.smartcampus.backend.dto.admin.DashboardStatsDTO;
import com.smartcampus.backend.dto.admin.ModeCountDTO;
import com.smartcampus.backend.repository.search.SearchHistoryLegRepository;
import com.smartcampus.backend.repository.search.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchHistoryLegRepository searchHistoryLegRepository;

    public DashboardStatsDTO getStats() {
        long totalSearches = searchHistoryRepository.count();
        long activeUsers = searchHistoryRepository.countDistinctActiveUsersSince(LocalDateTime.now().minusDays(30));
        double co2 = searchHistoryRepository.sumCo2Grams();

        List<ModeCountDTO> modes = searchHistoryLegRepository.countGroupedByMode().stream()
                .map(row -> new ModeCountDTO((String) row[0], (Long) row[1]))
                .toList();

        return DashboardStatsDTO.builder()
                .totalSearches(totalSearches)
                .activeUsersLast30Days(activeUsers)
                .totalCo2GramsSaved(co2)
                .mostUsedModes(modes)
                .build();
    }
}