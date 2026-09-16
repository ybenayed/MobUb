package com.ObservatoireCampus.mobile.model.admin

data class AdminStationVDto(
    val id: Long,
    val stationId: String,
    val nom: String?,
    val adresse: String?,
    val capacite: Int?,
    val latitude: Double?,
    val longitude: Double?
)

data class AdminStationVRequestDto(
    val stationId: String,
    val nom: String?,
    val adresse: String?,
    val capacite: Int?,
    val latitude: Double?,
    val longitude: Double?
)