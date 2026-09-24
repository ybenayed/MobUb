// model/search/LegDto.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Miroir de LegDTO.java côté backend.
 */
data class LegDto(
    val mode: String,
    val fromName: String?,
    val fromLat: Double = 0.0,
    val fromLon: Double = 0.0,
    val toName: String?,
    val toLat: Double = 0.0,
    val toLon: Double = 0.0,
    val startTime: Long,
    val endTime: Long,
    val distance: Double = 0.0,
    val rentedBike: Boolean = false,
    val routeName: String?,
    val geometry: List<GeoPointDto> = emptyList()
)