package com.ObservatoireCampus.mobile.model.search

/**
 * Corps envoyé à POST /api/itinerary.
 * Miroir exact de ItineraryRequestDTO.java côté backend.
 */
data class ItineraryRequestDto(
    val origin: SearchResultDto,
    val destination: SearchResultDto
)