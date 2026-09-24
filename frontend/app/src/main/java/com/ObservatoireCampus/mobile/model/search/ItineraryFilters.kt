package com.ObservatoireCampus.mobile.model.search


data class ItineraryFilters(
    val modes: Set<TransportModeUi> = setOf(TransportModeUi.WALK, TransportModeUi.TRANSIT),
    val date: String? = null,      // "yyyy-MM-dd", null pour aujourd'hui
    val time: String? = null,      // "HH:mm", null pour maintenant
    val arriveBy: Boolean = false, // false = "partir à", true = "arriver à"
    val sortBy: ItinerarySortOption = ItinerarySortOption.DURATION
) {
    val isPersonalBikeOnly: Boolean
        get() = modes == setOf(TransportModeUi.BICYCLE)
}