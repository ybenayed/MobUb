package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.ItineraryRequestDto
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

/**
 * Couche repository standard (même rôle que SearchRepository) :
 * fait l'intermédiaire entre ItineraryViewModel et Retrofit.
 */
class ItineraryRepository {

    suspend fun sendItinerary(origin: SearchResultDto, destination: SearchResultDto) {
        RetrofitClient.itineraryApi.sendItinerary(
            ItineraryRequestDto(origin = origin, destination = destination)
        )
    }
}