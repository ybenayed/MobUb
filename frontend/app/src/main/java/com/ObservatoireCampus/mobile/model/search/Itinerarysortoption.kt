// model/search/ItinerarySortOption.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Tri appliqué CÔTÉ CLIENT sur les résultats déjà reçus du backend.
 * ACCESSIBILITY retire avec le PMR : n'avait de sens qu'avec l'endpoint
 * /accessible, qui n'est plus appele depuis l'app.
 */
enum class ItinerarySortOption(val label: String) {
    DURATION("Le plus rapide"),
    LEAST_WALKING("Le moins de marche"),
    FEWEST_TRANSFERS("Le moins de correspondances"),
    ECO_FRIENDLY("Le plus écologique")
}

fun List<ItineraryOptionDto>.sortedByOption(option: ItinerarySortOption): List<ItineraryOptionDto> =
    when (option) {
        ItinerarySortOption.DURATION ->
            sortedBy { it.duration }

        ItinerarySortOption.LEAST_WALKING ->
            sortedBy { it.walkDistance }

        ItinerarySortOption.FEWEST_TRANSFERS ->
            sortedWith(compareBy({ it.transfers }, { it.duration }))

        ItinerarySortOption.ECO_FRIENDLY ->
            sortedBy { it.co2Grams }
    }