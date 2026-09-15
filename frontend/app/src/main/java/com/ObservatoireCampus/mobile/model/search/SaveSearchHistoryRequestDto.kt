// model/search/SaveSearchHistoryRequestDto.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Miroir de SaveSearchHistoryRequestDTO.java cote backend.
 * On reutilise directement ItineraryOptionDto (deja calcule par OTP) :
 * pas besoin de refaire un appel reseau pour sauvegarder.
 */
data class SaveSearchHistoryRequestDto(
    val origin: SearchResultDto,
    val destination: SearchResultDto,
    val itinerary: ItineraryOptionDto
)