package com.ObservatoireCampus.mobile.model.search

/**
 * Représente un résultat de recherche de lieu (autocomplétion).
 * - name : titre court affiché en gras (ex: "Kedge Business School")
 * - subtitle : complément d'adresse affiché en gris, plus petit (ex: "Talence, Gironde")
 * Utilisé pour positionner la caméra et le marqueur sur la carte.
 */
data class SearchResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val subtitle: String = ""
)