package com.ObservatoireCampus.mobile.model


data class BatimentDto(
    val id: Long,
    val name: String,
    val appartenance: String?,
    val fillColor: String?,
    val strokeColor: String?,
    val campusId: Long?,
    // 3 niveaux : Liste de contours -> Liste de points -> [longitude, latitude]
    val polygonCoordinates: List<List<List<Double>>> = emptyList(),
    val importedAt: String?
)