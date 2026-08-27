// model/search/ItineraryOptionDto.kt
package com.ObservatoireCampus.mobile.model.search

data class ItineraryOptionDto(
    val duration: Long,
    val legs: List<LegDto>
)