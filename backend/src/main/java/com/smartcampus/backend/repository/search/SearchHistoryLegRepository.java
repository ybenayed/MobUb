package com.smartcampus.backend.repository.search;

import com.smartcampus.backend.entity.search.SearchHistoryLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchHistoryLegRepository extends JpaRepository<SearchHistoryLeg, Long> {

    @Query("SELECT l.mode, COUNT(l) FROM SearchHistoryLeg l GROUP BY l.mode ORDER BY COUNT(l) DESC")
    List<Object[]> countGroupedByMode();
}