package com.smartcampus.backend.service.search;

import com.smartcampus.backend.dto.search.history.SaveSearchHistoryRequestDTO;
import com.smartcampus.backend.dto.search.history.SearchHistoryResponseDTO;
import com.smartcampus.backend.entity.User;
import com.smartcampus.backend.entity.search.SearchHistory;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.mapper.search.SearchHistoryMapper;
import com.smartcampus.backend.repository.UserRepository;
import com.smartcampus.backend.repository.search.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final SearchHistoryMapper mapper;

    @Transactional
    public SearchHistoryResponseDTO save(String username, SaveSearchHistoryRequestDTO request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + username));

        SearchHistory entity = mapper.toEntity(user, request);
        SearchHistory saved = searchHistoryRepository.save(entity);
        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<SearchHistoryResponseDTO> getHistoryForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + username));

        return searchHistoryRepository.findByUserIdOrderBySearchedAtDesc(user.getId())
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void delete(String username, Long historyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + username));

        SearchHistory entity = searchHistoryRepository.findByIdAndUserId(historyId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Historique introuvable ou n'appartient pas a l'utilisateur"));

        searchHistoryRepository.delete(entity);
    }
}