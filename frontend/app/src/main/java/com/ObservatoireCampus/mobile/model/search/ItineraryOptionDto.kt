package com.ObservatoireCampus.mobile.model.search


data class ItineraryOptionDto(
    val duration: Long,
    val transfers: Int,
    val walkDistance: Double,
    val co2Grams: Double,
    val accessibilityScore: Double,
    val profileLabel: String? = null,
    val legs: List<LegDto>
)