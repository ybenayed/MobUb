// ui/components/itinerary/ItineraryModeStyle.kt
package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.ui.graphics.Color

object ItineraryModeStyle {
    fun color(mode: String): Color = when (mode.uppercase()) {
        "WALK" -> Color(0xFF757575)
        "BUS" -> Color(0xFF2563EB)
        "TRAM" -> Color(0xFFDC2626)
        "RAIL", "TRAIN" -> Color(0xFF16A34A)
        "BICYCLE" -> Color(0xFFF59E0B)
        else -> Color(0xFF9333EA)
    }

    fun emoji(mode: String): String = when (mode.uppercase()) {
        "WALK" -> "🚶"
        "BUS" -> "🚌"
        "TRAM" -> "🚋"
        "RAIL", "TRAIN" -> "🚆"
        "BICYCLE" -> "🚲"
        else -> "➡️"
    }
}