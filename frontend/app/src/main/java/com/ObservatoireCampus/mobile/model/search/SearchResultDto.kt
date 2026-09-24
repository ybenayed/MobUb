package com.ObservatoireCampus.mobile.model.search

/**
 * Représente un résultat de recherche de lieu (autocomplétion).
 * Utilisé pour positionner et le marqueur sur la carte.
 */
data class SearchResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val subtitle: String = ""
)