package com.smartcampus.backend.service.search;

import com.smartcampus.backend.dto.search.ItineraryOptionDTO;

import java.util.Comparator;

/**
 * Un critere par endpoint de tri (/fastest, /least-walking, /fewest-transfers,
 * /eco, ). Centralise ici pour que le service et le controller
 * partagent la meme logique de comparaison.
 */
public enum ItinerarySortCriterion {

    FASTEST(Comparator.comparingLong(ItineraryOptionDTO::getDuration)),

    LEAST_WALKING(Comparator.comparingDouble(ItineraryOptionDTO::getWalkDistance)),

    FEWEST_TRANSFERS(Comparator.comparingInt(ItineraryOptionDTO::getTransfers)
            .thenComparingLong(ItineraryOptionDTO::getDuration)),

    ECO_FRIENDLY(Comparator.comparingDouble(ItineraryOptionDTO::getCo2Grams)),

    ACCESSIBILITY(Comparator.comparingDouble(ItineraryOptionDTO::getAccessibilityScore).reversed()
            .thenComparingLong(ItineraryOptionDTO::getDuration));

    private final Comparator<ItineraryOptionDTO> comparator;

    ItinerarySortCriterion(Comparator<ItineraryOptionDTO> comparator) {
        this.comparator = comparator;
    }

    public Comparator<ItineraryOptionDTO> comparator() {
        return comparator;
    }
}