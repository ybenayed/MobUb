package com.ObservatoireCampus.mobile.model.search

/**
 * État des filtres du panneau de recherche d'itinéraire.
 * Ne correspond PAS 1:1 au JSON envoyé au backend : c'est converti
 * en ItineraryRequestDto juste avant l'appel réseau (voir toRequestDto()).
 *
 * PMR retire : le filtre wheelchair et le tri ACCESSIBILITY sont supprimes
 * cote app mobile (l'endpoint /api/itinerary/accessible reste disponible
 * cote backend mais n'est plus appele depuis le client).
 */
data class ItineraryFilters(
    val modes: Set<TransportModeUi> = setOf(TransportModeUi.WALK, TransportModeUi.TRANSIT),
    val date: String? = null,      // "yyyy-MM-dd", null = aujourd'hui
    val time: String? = null,      // "HH:mm", null = maintenant
    val arriveBy: Boolean = false, // false = "partir à", true = "arriver à"
    val sortBy: ItinerarySortOption = ItinerarySortOption.DURATION
) {
    /** Vrai si le seul mode sélectionné est "vélo perso" -> déclenche /itinerary/bicycle. */
    val isPersonalBikeOnly: Boolean
        get() = modes == setOf(TransportModeUi.BICYCLE)
}