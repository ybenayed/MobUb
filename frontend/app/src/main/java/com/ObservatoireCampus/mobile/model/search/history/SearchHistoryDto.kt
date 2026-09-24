package com.ObservatoireCampus.mobile.model.search.history

/** Miroir de SearchHistoryResponseDTO.java cote backend. */
data class SearchHistoryDto(
    val id: Long,
    val originName: String?,
    val destinationName: String?,
    val departureTime: String?,
    val arrivalTime: String?,
    val duration: Long,
    val numberOfTransfers: Int,
    val walkDistance: Double?,
    val accessibilityScore: Double?,
    val co2Grams: Double?,
    val profileLabel: String?,
    val searchedAt: String?,
    val legs: List<SearchHistoryLegDto>
)