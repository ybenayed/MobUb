// model/search/ItinerarySortOption.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Tri appliqué CÔTÉ CLIENT sur les résultats déjà reçus du backend (aucun appel
 * réseau supplémentaire pour DURATION / LEAST_WALKING / FEWEST_TRANSFERS / ECO_FRIENDLY :
 * une seule requête POST /api/itinerary renvoie déjà toutes les métriques nécessaires,
 * on ne fait que re-trier la même liste en mémoire).
 *
 * ACCESSIBILITY n'a de sens que si on a interrogé /api/itinerary/accessible
 * (sinon accessibilityScore vaut 0.0 pour tout le monde et le tri est sans effet).
 */
enum class ItinerarySortOption(val label: String) {
    DURATION("Le plus rapide"),
    LEAST_WALKING("Le moins de marche"),
    FEWEST_TRANSFERS("Le moins de correspondances"),
    ECO_FRIENDLY("Le plus écologique"),
    ACCESSIBILITY("Le plus accessible (PMR)")
}

/**
 * IMPORTANT : on trie ici sur les champs natifs renvoyés par le backend
 * (transfers, walkDistance, co2Grams, accessibilityScore), calculés/récupérés
 * depuis OTP. On ne recalcule plus rien à partir des legs comme avant
 * (l'ancien "legs.size" pour les correspondances était faux : ça comptait
 * les segments, pas les changements de véhicule).
 */
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

        ItinerarySortOption.ACCESSIBILITY ->
            sortedWith(compareByDescending<ItineraryOptionDto> { it.accessibilityScore }.thenBy { it.duration })
    }