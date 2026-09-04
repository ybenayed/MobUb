// model/search/LegDto.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Miroir de LegDTO.java côté backend.
 * distance   : mètres, utile pour afficher "350 m à pied" sur un leg de marche.
 * rentedBike : true = vélo en libre-service (Vcub), permet de distinguer
 *              visuellement 🚲 (perso) de 🛴 (Vcub) alors que mode="BICYCLE" pour les deux.
 */
data class LegDto(
    val mode: String,
    val fromName: String?,
    val toName: String?,
    val startTime: Long,
    val endTime: Long,
    val distance: Double = 0.0,
    val rentedBike: Boolean = false,
    val routeName: String?,
    val geometry: List<GeoPointDto> = emptyList()
)