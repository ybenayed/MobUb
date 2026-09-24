package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.search.ItineraryRequestDto
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface ItineraryApi {

    @POST("/api/itinerary")
    suspend fun computeItinerary(@Body request: ItineraryRequestDto): Response<List<ItineraryOptionDto>>

    @POST("/api/itinerary/bicycle")
    suspend fun computeBicycleItineraries(@Body request: ItineraryRequestDto): Response<List<ItineraryOptionDto>>

}