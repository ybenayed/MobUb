package com.ObservatoireCampus.mobile.model.weather

/**
 * Mirroir du  cote backend.
 */
data class WeatherRequestDto(
    val latitude: Double,
    val longitude: Double,
    val date: String,        // format "yyyy-MM-dd"
    val time: String? = null // format "HH:mm:ss"
)