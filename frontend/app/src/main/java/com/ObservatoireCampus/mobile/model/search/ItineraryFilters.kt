package com.ObservatoireCampus.mobile.model.search

/**
 * État des filtres du panneau de recherche d'itinéraire.
 * Ne correspond PAS 1:1 au JSON envoyé au backend : c'est converti
 * en ItineraryRequestDto juste avant l'appel réseau (voir toRequestDto()).
 *
 * numItineraries a été retiré : le backend décide seul du nombre de propositions
 * et renvoie systématiquement tout ce qu'il trouve (voir ItineraryRequestDTO.java
 * côté backend, qui n'a plus ce champ non plus).
 */
data class ItineraryFilters(
    val modes: Set<TransportModeUi> = setOf(TransportModeUi.WALK, TransportModeUi.TRANSIT),
    val date: String? = null,      // "yyyy-MM-dd", null = aujourd'hui
    val time: String? = null,      // "HH:mm", null = maintenant
    val arriveBy: Boolean = false, // false = "partir à", true = "arriver à"
    val wheelchair: Boolean = false,
    val sortBy: ItinerarySortOption = ItinerarySortOption.DURATION
) {
    /** Vrai si le seul mode sélectionné est "vélo perso" -> déclenche /itinerary/bicycle. */
    val isPersonalBikeOnly: Boolean
        get() = modes == setOf(TransportModeUi.BICYCLE)
}