package com.smartcampus.backend.controller.search;

import com.smartcampus.backend.dto.search.SearchResultDTO;
import com.smartcampus.backend.service.search.GeocodingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//Expose la recherche de lieux  
@RestController
public class SearchController {

    private final GeocodingService geocodingService;

    public SearchController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @GetMapping("/api/search")
    public List<SearchResultDTO> search(@RequestParam("q") String query) {
        if (query == null || query.isBlank() || query.length() < 3) {
            return List.of();
        }
        return geocodingService.search(query);
    }
}