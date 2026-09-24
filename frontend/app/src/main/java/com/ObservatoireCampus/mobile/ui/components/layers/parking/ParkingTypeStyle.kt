package com.ObservatoireCampus.mobile.ui.components.layers.parking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Layers
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel // AJOUT

object ParkingTypeStyle {

    fun color(taType: String): Color = when {
        taType.contains("PARC_RELAIS") -> Color(0xFF2E7D32)
        taType.contains("GRATUIT") -> Color(0xFF1976D2)
        taType.contains("HORAIRE") -> Color(0xFFEF6C00)
        taType.contains("ABONNE") -> Color(0xFF7B1FA2)
        else -> Color(0xFF616161)
    }

    fun icon(taType: String): ImageVector = when {
        taType.contains("PARC_RELAIS") -> Icons.Default.DirectionsSubway
        taType.contains("GRATUIT") -> Icons.Default.LocalParking
        else -> Icons.Default.DirectionsCar
    }

    suspend fun label(taType: String, languageViewModel: LanguageViewModel): String {
        val key = when {
            taType.contains("PARC_RELAIS") -> "Parc relais"
            taType.contains("GRATUIT") -> "Gratuit"
            taType.contains("HORAIRE") -> "Payant horaire"
            taType.contains("ABONNE") -> "Abonnés"
            else -> taType.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
        }
        return languageViewModel.translate(key)
    }

    fun markerLetter(taType: String): String = when {
        taType.contains("PARC_RELAIS") -> "R"
        taType.contains("GRATUIT") -> "G"
        taType.contains("HORAIRE") -> "H"
        taType.contains("ABONNE") -> "A"
        else -> "P"
    }

    fun structureIcon(type: String?): ImageVector = when (type) {
        "SILO" -> Icons.Default.Layers
        "ENTERRE" -> Icons.Default.Elevator
        "SURFACE" -> Icons.Default.Terrain
        "MIXTE" -> Icons.Default.Garage
        else -> Icons.Default.LocalParking
    }

    suspend fun structureLabel(type: String?, languageViewModel: LanguageViewModel): String {
        val key = when (type) {
            "SILO" -> "Silo"
            "ENTERRE" -> "Souterrain"
            "SURFACE" -> "Surface"
            "MIXTE" -> "Mixte"
            else -> "Parking"
        }
        return languageViewModel.translate(key)
    }
}