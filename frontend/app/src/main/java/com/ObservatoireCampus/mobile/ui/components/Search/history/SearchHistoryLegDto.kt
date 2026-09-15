// model/search/history/SearchHistoryLegDto.kt
package com.ObservatoireCampus.mobile.model.search.history

/** Miroir de SearchHistoryLegResponseDTO.java cote backend. */
data class SearchHistoryLegDto(
    val mode: String,
    val fromName: String?,
    val fromLat: Double = 0.0,
    val fromLon: Double = 0.0,
    val toName: String?,
    val toLat: Double = 0.0,
    val toLon: Double = 0.0,
    val distance: Double?,
    val rentedBike: Boolean?,
    val routeName: String?,
    val startTime: String?,   // LocalDateTime serialise en ISO-8601 par Jackson (ex: "2026-09-10T14:30:00")
    val endTime: String?
)