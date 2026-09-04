// model/search/ItineraryOptionDto.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Miroir de ItineraryOptionDTO.java côté backend.
 * Avant, ce DTO n'avait que duration+legs : impossible de trier par marche,
 * correspondances, CO2 ou accessibilité sans le reste des champs. Maintenant
 * toutes les métriques calculées par OTP (et transmises telles quelles par
 * le backend) sont disponibles ici, ce qui permet un tri 100% client-side
 * (voir ItinerarySortOption.sortedByOption()).
 *
 * profileLabel : rempli uniquement pour les variantes de vélo perso
 * (ex: "Vélo — le plus rapide"), null sinon.
 */
data class ItineraryOptionDto(
    val duration: Long,
    val transfers: Int,
    val walkDistance: Double,
    val co2Grams: Double,
    val accessibilityScore: Double,
    val profileLabel: String? = null,
    val legs: List<LegDto>
)