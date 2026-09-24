package com.smartcampus.backend.dto.search.history;

import com.smartcampus.backend.dto.search.ItineraryOptionDTO;
import com.smartcampus.backend.dto.search.SearchResultDTO;
import lombok.Getter;
import lombok.Setter;

//Envoye par le client quand il choisit de sauvegarder un itineraire parmi

@Getter
@Setter
public class SaveSearchHistoryRequestDTO {
    private SearchResultDTO origin;
    private SearchResultDTO destination;
    private ItineraryOptionDTO itinerary;
}