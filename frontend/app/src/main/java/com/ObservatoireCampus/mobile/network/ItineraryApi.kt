package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.search.ItineraryRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ItineraryApi {

    @POST("/api/itinerary") // <-- Ajout du / au début
    suspend fun sendItinerary(@Body request: ItineraryRequestDto)
}