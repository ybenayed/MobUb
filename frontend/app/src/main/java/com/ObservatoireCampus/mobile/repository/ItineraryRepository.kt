// repository/ItineraryRepository.kt
package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.model.search.ItineraryRequestDto
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class ItineraryRepository {

    /** Liste vide si 204 (rien trouvé) ou erreur HTTP/réseau. */
    suspend fun computeItinerary(
        origin: SearchResultDto,
        destination: SearchResultDto
    ): List<ItineraryOptionDto> {
        val response = RetrofitClient.itineraryApi.computeItinerary(
            ItineraryRequestDto(origin = origin, destination = destination)
        )
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }
}