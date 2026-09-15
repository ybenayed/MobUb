package com.smartcampus.backend.repository.search;

import com.smartcampus.backend.entity.search.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(Long userId);

    Optional<SearchHistory> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COALESCE(SUM(s.co2Grams), 0) FROM SearchHistory s")
    double sumCo2Grams();

    @Query("SELECT COUNT(DISTINCT s.user.id) FROM SearchHistory s WHERE s.searchedAt >= :since")
    long countDistinctActiveUsersSince(LocalDateTime since);
}