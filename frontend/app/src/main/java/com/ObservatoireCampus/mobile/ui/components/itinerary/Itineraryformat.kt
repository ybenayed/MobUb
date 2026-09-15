// ui/components/itinerary/ItineraryFormat.kt
package com.ObservatoireCampus.mobile.ui.components.itinerary

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ItineraryFormat {
    fun duration(seconds: Long): String {
        val minutes = seconds / 60
        return if (minutes < 60) "$minutes min" else "${minutes / 60} h ${minutes % 60} min"
    }

    fun walkDistance(meters: Double): String =
        if (meters < 1000) "${meters.toInt()} m à pied" else "%.1f km à pied".format(meters / 1000)

    fun co2(grams: Double): String =
        if (grams < 1000) "${grams.toInt()} g CO2" else "%.1f kg CO2".format(grams / 1000)

    fun transfers(count: Int): String = when (count) {
        0 -> "Trajet direct"
        1 -> "1 correspondance"
        else -> "$count correspondances"
    }

    /** Formate un timestamp epoch millis en heure locale "HH:mm". "--:--" si absent/invalide. */
    fun clockTime(epochMillis: Long): String {
        if (epochMillis <= 0L) return "--:--"
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date(epochMillis))
    }
}