// model/search/LegDto.kt
package com.ObservatoireCampus.mobile.model.search

data class LegDto(
    val mode: String,
    val fromName: String?,
    val toName: String?,
    val startTime: Long,
    val endTime: Long,
    val routeName: String?,
    val geometry: List<GeoPointDto> = emptyList()
)