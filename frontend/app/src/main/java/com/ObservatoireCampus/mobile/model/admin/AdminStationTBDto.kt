package com.ObservatoireCampus.mobile.model.admin

data class AdminStationTBDto(
    val id: Long,
    val stopId: String,
    val nom: String?,
    val stopAreaRef: String?,
    val mode: String?,        // "BUS" ou "TRAM"
    val latitude: Double?,
    val longitude: Double?,
    val lines: List<String>?
)

data class AdminStationTBRequestDto(
    val stopId: String,
    val nom: String?,
    val stopAreaRef: String?,
    val mode: String?,
    val latitude: Double?,
    val longitude: Double?,
    val lines: List<String>?
)