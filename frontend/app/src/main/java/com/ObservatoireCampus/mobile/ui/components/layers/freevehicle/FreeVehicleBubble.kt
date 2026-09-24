package com.ObservatoireCampus.mobile.ui.components.freevehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleTypeStyle
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.ui.components.BubbleErrorContent
@Composable
fun FreeVehicleBubble(
    detail: FreeVehicleDetailDto?,
    loading: Boolean,
    onClose: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    // États traduits
    var textHeaderLabel by remember { mutableStateOf("VÉHICULE EN LIBRE-SERVICE") }
    var textVehicleFallback by remember { mutableStateOf("Véhicule") }
    var textDispoLabel by remember { mutableStateOf("Disponibilité") }
    var textAutonomieLabel by remember { mutableStateOf("Autonomie") }
    var textDernierePosition by remember { mutableStateOf("Dernière position") }
    var textFermer by remember { mutableStateOf("Fermer") }
    var textIndisponible by remember { mutableStateOf("Informations indisponibles") }

    // États dynamiques de statut
    var textHorsService by remember { mutableStateOf("Hors service") }
    var textReserve by remember { mutableStateOf("Réservé") }
    var textDisponible by remember { mutableStateOf("Disponible") }

    // États de temps relatifs
    var textIlYa by remember { mutableStateOf("il y a") }
    var textMin by remember { mutableStateOf("min") }
    var textHeureAbbrev by remember { mutableStateOf("h") }
    var textSecondesAbbrev by remember { mutableStateOf("s") }

    var vehicleTypeLabel by remember { mutableStateOf("") }

    LaunchedEffect(currentLanguage, detail) {
        textHeaderLabel = languageViewModel.translate("VÉHICULE EN LIBRE-SERVICE")
        textVehicleFallback = languageViewModel.translate("Véhicule")
        textDispoLabel = languageViewModel.translate("Disponibilité")
        textAutonomieLabel = languageViewModel.translate("Autonomie")
        textDernierePosition = languageViewModel.translate("Dernière position")
        textFermer = languageViewModel.translate("Fermer")
        textIndisponible = languageViewModel.translate("Informations indisponibles")

        textHorsService = languageViewModel.translate("Hors service")
        textReserve = languageViewModel.translate("Réservé")
        textDisponible = languageViewModel.translate("Disponible")

        textIlYa = languageViewModel.translate("il y a")
        textMin = languageViewModel.translate("min")
        textHeureAbbrev = languageViewModel.translate("h")
        textSecondesAbbrev = languageViewModel.translate("s")

        vehicleTypeLabel = if (detail != null) {
            try {
                FreeVehicleTypeStyle.label(detail.vehicleTypeId, languageViewModel)
            } catch (e: Exception) {
                textVehicleFallback
            }
        } else {
            textVehicleFallback
        }
    }

    Card(
        modifier = modifier.widthIn(max = 340.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = textHeaderLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = vehicleTypeLabel.ifEmpty { textVehicleFallback },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = textFermer,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                }
                errorMessage != null -> {
                    BubbleErrorContent(
                        message = errorMessage,
                        onRetry = onRetry,
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage
                    )
                }
                detail == null -> {
                    Text(
                        text = textIndisponible,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        val (statusText, statusColor) = when {
                            detail.isDisabled == true -> textHorsService to Color(0xFFD32F2F)
                            detail.isReserved == true -> textReserve to Color(0xFFFBC02D)
                            else -> textDisponible to Color(0xFF4CAF50)
                        }

                        InfoRow(
                            label = textDispoLabel,
                            value = statusText,
                            valueColor = statusColor
                        )

                        detail.currentRangeMeters?.let {
                            InfoRow(
                                label = textAutonomieLabel,
                                value = "${it / 1000} km",
                                valueColor = Color(0xFF1976D2)
                            )
                        }

                        detail.lastReported?.let {
                            InfoRow(
                                label = textDernierePosition,
                                value = formatLastReported(it, textIlYa, textMin, textHeureAbbrev, textSecondesAbbrev),
                                valueColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

private fun formatLastReported(iso: String, ilYa: String, min: String, h: String, s: String): String {
    return try {
        val instant = java.time.Instant.parse(iso)
        val now = java.time.Instant.now()
        val seconds = java.time.Duration.between(instant, now).seconds
        when {
            seconds < 60 -> "$ilYa ${seconds}$s"
            seconds < 3600 -> "$ilYa ${seconds / 60} $min"
            else -> "$ilYa ${seconds / 3600} $h"
        }
    } catch (e: Exception) {
        iso
    }
}