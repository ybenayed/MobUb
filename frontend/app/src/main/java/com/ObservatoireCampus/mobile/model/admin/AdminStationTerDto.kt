package com.ObservatoireCampus.mobile.model.admin

data class AdminStationTerDto(
    val id: Long,
    val navitiaId: String,
    val nom: String?,
    val latitude: Double?,
    val longitude: Double?,
    val distanceCentreMetres: Double?
)

data class AdminStationTerRequestDto(
    val navitiaId: String,
    val nom: String?,
    val latitude: Double?,
    val longitude: Double?
)