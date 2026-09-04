// ui/components/itinerary/ItineraryModeStyle.kt
package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.ui.graphics.Color
import com.ObservatoireCampus.mobile.model.search.LegDto

object ItineraryModeStyle {
    fun color(mode: String): Color = when (mode.uppercase()) {
        "WALK" -> Color(0xFF757575)
        "BUS" -> Color(0xFF2563EB)
        "TRAM" -> Color(0xFFDC2626)
        "SUBWAY" -> Color(0xFF7C3AED)
        "RAIL", "TRAIN" -> Color(0xFF16A34A)
        "FERRY" -> Color(0xFF0891B2)
        "BICYCLE" -> Color(0xFFF59E0B)
        "CAR" -> Color(0xFF334155)
        else -> Color(0xFF9333EA)
    }

    fun emoji(mode: String): String = when (mode.uppercase()) {
        "WALK" -> "🚶"
        "BUS" -> "🚌"
        "TRAM" -> "🚋"
        "SUBWAY" -> "🚇"
        "RAIL", "TRAIN" -> "🚆"
        "FERRY" -> "⛴️"
        "BICYCLE" -> "🚲"
        "CAR" -> "🚗"
        else -> "➡️"
    }

    /**
     * Variante qui distingue vélo perso (🚲) et Vcub/libre-service (🛴) alors
     * qu'OTP renvoie le même mode "BICYCLE" pour les deux (voir LegDTO.rentedBike
     * côté backend). A utiliser à la place de emoji(leg.mode) partout où on
     * affiche un LegDto complet.
     */
    fun emojiForLeg(leg: LegDto): String =
        if (leg.mode.equals("BICYCLE", ignoreCase = true) && leg.rentedBike) "🛴" else emoji(leg.mode)
}