// model/search/TransportModeUi.kt
package com.ObservatoireCampus.mobile.model.search


enum class TransportModeUi(val apiValue: String, val label: String, val emoji: String) {
    WALK("WALK", "Marche", "🚶"),
    TRANSIT("TRANSIT", "Transport en commun", "🚌"),
    BICYCLE("BICYCLE", "Vélo perso", "🚲"),
    BICYCLE_RENT("BICYCLE_RENT", "Vélo libre-service (Vcub)", "🛴")
}