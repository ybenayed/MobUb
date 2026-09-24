package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.*
import com.ObservatoireCampus.mobile.network.RetrofitClient

/**
 * Uniquement le calcul d'itineraires (OTP). L'enregistrement dans l'historique  est gere par SearchHistoryRepository (voir ItineraryViewModel.saveToHistory qui appelle searchHistoryRepository.saveToHistory et non celui-ci).
 */
class ItineraryRepository {

    suspend fun computeItinerary(
        origin: SearchResultDto,
        destination: SearchResultDto,
        filters: ItineraryFilters
    ): List<ItineraryOptionDto> {
        val response = RetrofitClient.itineraryApi.computeItinerary(
            filters.toRequestDto(origin, destination)
        )
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun computeBicycleItineraries(
        origin: SearchResultDto,
        destination: SearchResultDto,
        filters: ItineraryFilters
    ): List<ItineraryOptionDto> {
        val response = RetrofitClient.itineraryApi.computeBicycleItineraries(
            filters.toRequestDto(origin, destination)
        )
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }
}