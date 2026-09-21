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
import com.ObservatoireCampus.mobile.model.station.PassageDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage

@Composable
fun StationTBBubble(
    station: StationTBPositionDto,
    passages: List<PassageDto>,
    loading: Boolean,
    onClose: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    // États pour les textes traduits
    var textFermer by remember { mutableStateOf("Fermer") }
    var textAucunPassage by remember { mutableStateOf("Aucun passage prévu") }
    var textInconnu by remember { mutableStateOf("Direction inconnue") }
    var textRetard by remember { mutableStateOf("Retard") }
    var textTram by remember { mutableStateOf("Tram") }
    var textBus by remember { mutableStateOf("Bus") }
    var textReessayer by remember { mutableStateOf("Reessayer") }
    var textErreur by remember { mutableStateOf("") }

    LaunchedEffect(currentLanguage) {
        textFermer = languageViewModel.translate("Fermer")
        textAucunPassage = languageViewModel.translate("Aucun passage prévu")
        textInconnu = languageViewModel.translate("Direction inconnue")
        textRetard = languageViewModel.translate("Retard")
        textTram = languageViewModel.translate("Tram")
        textBus = languageViewModel.translate("Bus")
        textReessayer = languageViewModel.translate("Reessayer")
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
                        text = station.mode.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (station.mode == "TRAM") Color(0xFF1976D2) else Color(0xFFEF6C00),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = station.nom,
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
                passages.isEmpty() -> {
                    Text(
                        text = textAucunPassage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        passages.take(5).forEach { p ->
                            PassageRow(
                                passage = p,
                                mode = station.mode,
                                labelTram = textTram,
                                labelBus = textBus,
                                labelInconnu = textInconnu,
                                labelRetard = textRetard
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PassageRow(
    passage: PassageDto,
    mode: String,
    labelTram: String,
    labelBus: String,
    labelInconnu: String,
    labelRetard: String
) {
    val hTheorique = formatHeure(passage.heureTheorique)
    val retardSecondes = passage.retardSecondes ?: 0L
    val estEnRetard = retardSecondes > 45L

    val modeLabel = if (mode == "TRAM") labelTram else labelBus
    val ligneCode = cleanLigne(passage.ligne)
    val ligneLabel = ligneCode?.let { "$modeLabel $it" } ?: modeLabel
    val modeColor = if (mode == "TRAM") Color(0xFF1976D2) else Color(0xFFEF6C00)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Text(
                text = ligneLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = modeColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = passage.destination ?: passage.direction ?: labelInconnu,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = hTheorique,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (estEnRetard) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
            )

            if (estEnRetard) {
                Text(
                    text = "$labelRetard +${retardSecondes / 60} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

private fun cleanLigne(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    if (!raw.contains(":")) return raw
    val segments = raw.split(":")
    val lineIndex = segments.indexOfFirst { it.equals("line", ignoreCase = true) }
    return if (lineIndex != -1 && lineIndex + 1 < segments.size) {
        segments[lineIndex + 1]
    } else {
        segments.getOrNull(segments.size - 2)?.takeIf { it.isNotBlank() } ?: segments.last()
    }
}

private fun formatHeure(raw: String?): String {
    if (raw.isNullOrBlank()) return "--:--"
    val timePart = raw.substringAfter("T", raw)
    val segments = timePart.split(":")
    return if (segments.size >= 2) {
        val heures = segments[0].takeLast(2).padStart(2, '0')
        val minutes = segments[1].take(2).padStart(2, '0')
        "$heures:$minutes"
    } else {
        "--:--"
    }
}