package com.smartcampus.backend.controller.search;

import com.smartcampus.backend.dto.search.history.SaveSearchHistoryRequestDTO;
import com.smartcampus.backend.dto.search.history.SearchHistoryResponseDTO;
import com.smartcampus.backend.service.search.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Historique des recherches d'itineraire, par utilisateur connecte (JWT)
@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @PostMapping
    public ResponseEntity<SearchHistoryResponseDTO> save(Authentication authentication,
                                                            @RequestBody SaveSearchHistoryRequestDTO request) {
        String username = authentication.getName();
        SearchHistoryResponseDTO saved = searchHistoryService.save(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/me")
    public ResponseEntity<List<SearchHistoryResponseDTO>> getMyHistory(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(searchHistoryService.getHistoryForUser(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        searchHistoryService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}