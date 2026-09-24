package com.ObservatoireCampus.mobile.ui.components.station

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
import com.ObservatoireCampus.mobile.model.station.StationVDetailDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage

@Composable
fun StationVBubble(
    position: StationVPositionDto,
    detail: StationVDetailDto?,
    loading: Boolean,
    onClose: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    // États pour les textes traduits
    var textHeaderLabel by remember { mutableStateOf("STATION VÉLO") }
    var textStatutLabel by remember { mutableStateOf("Statut") }
    var textEnService by remember { mutableStateOf("En service") }
    var textHorsService by remember { mutableStateOf("Hors service") }
    var textVelosDispo by remember { mutableStateOf("Vélos disponibles") }
    var textClassiques by remember { mutableStateOf("  • Classiques") }
    var textElectriques by remember { mutableStateOf("  • Électriques") }
    var textPlacesLibres by remember { mutableStateOf("Places libres") }
    var textCapacite by remember { mutableStateOf("Capacité totale") }
    var textMisAJour by remember { mutableStateOf("Mis à jour") }
    var textFermer by remember { mutableStateOf("Fermer") }
    var textIndisponible by remember { mutableStateOf("Informations indisponibles") }
    var textReessayer by remember { mutableStateOf("Reessayer") }
    var textErreur by remember { mutableStateOf("") }

    // États de calcul de temps relatifs (Traductions dynamiques)
    var textIlYa by remember { mutableStateOf("il y a") }
    var textMin by remember { mutableStateOf("min") }
    var textHeureAbbrev by remember { mutableStateOf("h") }
    var textSecondesAbbrev by remember { mutableStateOf("s") }

    LaunchedEffect(detail, currentLanguage) {
        textHeaderLabel = languageViewModel.translate("STATION VÉLO")
        textStatutLabel = languageViewModel.translate("Statut")
        textEnService = languageViewModel.translate("En service")
        textHorsService = languageViewModel.translate("Hors service")
        textVelosDispo = languageViewModel.translate("Vélos disponibles")
        textClassiques = languageViewModel.translate("  • Classiques")
        textElectriques = languageViewModel.translate("  • Électriques")
        textPlacesLibres = languageViewModel.translate("Places libres")
        textCapacite = languageViewModel.translate("Capacité totale")
        textMisAJour = languageViewModel.translate("Mis à jour")
        textFermer = languageViewModel.translate("Fermer")
        textIndisponible = languageViewModel.translate("Informations indisponibles")
        textReessayer = languageViewModel.translate("Reessayer")

        textIlYa = languageViewModel.translate("il y a")
        textMin = languageViewModel.translate("min")
        textHeureAbbrev = languageViewModel.translate("h")
        textSecondesAbbrev = languageViewModel.translate("s")
    }

    LaunchedEffect(errorMessage, currentLanguage) {
        textErreur = errorMessage?.let { languageViewModel.translate(it) } ?: ""
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
                        color = Color(0xFF00897B),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail?.nom ?: position.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
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
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = textErreur.ifBlank { errorMessage },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = onRetry) {
                            Text(textReessayer)
                        }
                    }
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

                        val enService = detail.enService ?: true
                        InfoRow(
                            label = textStatutLabel,
                            value = if (enService) textEnService else textHorsService,
                            valueColor = if (enService) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                        )

                        val velos = detail.velosDisponibles ?: 0
                        InfoRow(
                            label = textVelosDispo,
                            value = "$velos",
                            valueColor = if (velos > 0) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                            isBold = true
                        )

                        if (velos > 0) {
                            detail.velosClassiques?.let {
                                if (it > 0) InfoRow(label = textClassiques, value = "$it")
                            }
                            detail.velosElectriques?.let {
                                if (it > 0) InfoRow(label = textElectriques, value = "$it", valueColor = Color(0xFF1976D2))
                            }
                        }

                        val places = detail.placesDisponibles ?: 0
                        InfoRow(
                            label = textPlacesLibres,
                            value = "$places",
                            valueColor = if (places > 0) MaterialTheme.colorScheme.onSurface else Color(0xFFD32F2F)
                        )

                        detail.capacite?.let {
                            InfoRow(label = textCapacite, value = "$it")
                        }

                        detail.derniereMaj?.let {
                            InfoRow(
                                label = textMisAJour,
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
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
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
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
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