// model/search/SaveSearchHistoryRequestDto.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Miroir de SaveSearchHistoryRequestDTO.java cote backend.
 */
data class SaveSearchHistoryRequestDto(
    val origin: SearchResultDto,
    val destination: SearchResultDto,
    val itinerary: ItineraryOptionDto
)