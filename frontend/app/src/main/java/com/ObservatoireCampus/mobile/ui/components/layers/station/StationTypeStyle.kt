package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Tram
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Train
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

object StationTypeStyle {
    fun color(key: String): Color = when (key) {
        "TRAM" -> Color(0xFF1976D2)
        "BUS" -> Color(0xFFEF6C00)
        "VELO" -> Color(0xFF2E7D32)
        "TER" -> Color(0xFF6A1B9A)
        else -> Color.Gray
    }

    fun icon(key: String): ImageVector = when (key) {
        "TRAM" -> Icons.Default.Tram
        "BUS" -> Icons.Default.DirectionsBus
        "VELO" -> Icons.Default.DirectionsBike
        "TER" -> Icons.Default.Train
        else -> Icons.Default.Place
    }


    suspend fun label(key: String, languageViewModel: LanguageViewModel): String {
        val rawLabel = when (key) {
            "TRAM" -> "Tram"
            "BUS" -> "Bus"
            "VELO" -> "Vélo"
            "TER" -> "TER"
            else -> key
        }
        return languageViewModel.translate(rawLabel)
    }

    fun markerLetter(key: String): String = when (key) {
        "TRAM" -> "T"
        "BUS" -> "B"
        "VELO" -> "V"
        "TER" -> "R"
        else -> "?"
    }
}