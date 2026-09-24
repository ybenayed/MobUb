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
import com.ObservatoireCampus.mobile.model.station.PassageTerDto
import com.ObservatoireCampus.mobile.model.station.StationTerPositionDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage

@Composable
fun StationTerBubble(
    station: StationTerPositionDto,
    passages: List<PassageTerDto>,
    loading: Boolean,
    onClose: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    var textFermer by remember { mutableStateOf("Fermer") }
    var textAucunPassage by remember { mutableStateOf("Aucun passage prevu") }
    var textInconnu by remember { mutableStateOf("Direction inconnue") }
    var textRetard by remember { mutableStateOf("Retard") }
    var textTrain by remember { mutableStateOf("Train") }
    var textReessayer by remember { mutableStateOf("Reessayer") }
    var textErreur by remember { mutableStateOf("") }

    LaunchedEffect(currentLanguage) {
        textFermer = languageViewModel.translate("Fermer")
        textAucunPassage = languageViewModel.translate("Aucun passage prevu")
        textInconnu = languageViewModel.translate("Direction inconnue")
        textRetard = languageViewModel.translate("Retard")
        textTrain = languageViewModel.translate("Train")
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
                        text = "TER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6A1B9A),
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
                            PassageTerRow(
                                passage = p,
                                defaultTrainLabel = textTrain,
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
private fun PassageTerRow(
    passage: PassageTerDto,
    defaultTrainLabel: String,
    labelInconnu: String,
    labelRetard: String
) {
    // Temps reel
    val heureBrute = if (passage.tempsReel) {
        passage.heurePrevue ?: passage.heureTheorique
    } else {
        passage.heureTheorique ?: passage.heurePrevue
    }
    val heureAffichee = formatHeure(heureBrute)

    val retardSecondes = passage.retardSecondes ?: 0L
    val estEnRetard = retardSecondes > 45L

    val numeroTrain = when {
        !passage.ligne.isNullOrBlank() -> passage.ligne
        !passage.destination.isNullOrBlank() -> passage.destination
        else -> defaultTrainLabel
    }

    val directionReelle = cleanDirection(passage.direction, labelInconnu)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = numeroTrain,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A1B9A),
                    maxLines = 1
                )

                Text(
                    text = directionReelle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = heureAffichee,
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

private fun formatHeure(raw: String?): String {
    if (raw.isNullOrBlank()) return "--:--"

    Regex("""^(\d{1,2}):(\d{2})$""").matchEntire(raw.trim())?.let {
        return "${it.groupValues[1].padStart(2, '0')}:${it.groupValues[2]}"
    }

    val timePart = raw.substringAfter('T', raw).trim()
    Regex("""^(\d{2}):(\d{2})""").find(timePart)?.let {
        return "${it.groupValues[1]}:${it.groupValues[2]}"
    }
    Regex("""^(\d{2})(\d{2})(\d{2})?$""").find(timePart)?.let {
        return "${it.groupValues[1]}:${it.groupValues[2]}"
    }
    return "--:--"
}

private fun cleanDirection(raw: String?, fallback: String): String {
    if (raw.isNullOrBlank()) return fallback
    return raw.replace(Regex("\\s*\\(.*?\\)"), "").trim()
}