// ui/components/itinerary/ItineraryFormat.kt
package com.ObservatoireCampus.mobile.ui.components.itinerary

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
}