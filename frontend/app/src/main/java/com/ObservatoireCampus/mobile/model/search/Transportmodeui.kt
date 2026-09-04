// model/search/TransportModeUi.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Modes de transport proposés dans le panneau de filtres.
 * apiValue doit correspondre à ce que OtpItineraryService.java sait interpréter
 * (voir BIKE_RENTAL_ALIASES côté backend : "BICYCLE_RENTAL", "BICYCLE_RENT", "VCUB").
 */
enum class TransportModeUi(val apiValue: String, val label: String, val emoji: String) {
    WALK("WALK", "Marche", "🚶"),
    TRANSIT("TRANSIT", "Transport en commun", "🚌"),
    BICYCLE("BICYCLE", "Vélo perso", "🚲"),
    BICYCLE_RENT("BICYCLE_RENT", "Vélo libre-service (Vcub)", "🛴")
}