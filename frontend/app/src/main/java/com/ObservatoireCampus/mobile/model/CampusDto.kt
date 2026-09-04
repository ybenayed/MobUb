package com.ObservatoireCampus.mobile.model

data class CampusDto(
    val id: Long,
    val name: String,
    val city: String,
    val centerLat: Double,
    val centerLng: Double,
    val perimeterMeters: Double,
    // 3 niveaux, comme BatimentDto : Liste de PARTIES -> Liste de points -> [longitude, latitude]
    // (un campus en 2 blocs disjoints = 2 parties dans cette liste)
    val polygonCoordinates: List<List<List<Double>>> = emptyList(),
    val importedAt: String
)