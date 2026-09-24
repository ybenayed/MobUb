package com.ObservatoireCampus.mobile.ui.components.location

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import org.osmdroid.util.GeoPoint
import kotlin.math.roundToInt

@Composable
fun LocationBubble(
    point: GeoPoint?,
    loading: Boolean,
    accuracyMeters: Float? = null,
    onClose: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    // États pour stocker les textes traduits
    var translatedHeader by remember { mutableStateOf("MA POSITION") }
    var translatedSubHeader by remember { mutableStateOf("Vous êtes ici") }
    var translatedUnavailable by remember { mutableStateOf("Position indisponible") }
    var translatedLatitude by remember { mutableStateOf("Latitude") }
    var translatedLongitude by remember { mutableStateOf("Longitude") }
    var translatedPrecision by remember { mutableStateOf("Précision") }

    LaunchedEffect(currentLanguage) {
        translatedHeader = languageViewModel.translate("MA POSITION")
        translatedSubHeader = languageViewModel.translate("Vous êtes ici")
        translatedUnavailable = languageViewModel.translate("Position indisponible")
        translatedLatitude = languageViewModel.translate("Latitude")
        translatedLongitude = languageViewModel.translate("Longitude")
        translatedPrecision = languageViewModel.translate("Précision")
    }

    Card(
        modifier = modifier.widthIn(max = 340.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {

            // EN-TÊTE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = translatedHeader,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = translatedSubHeader,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
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
                point == null -> {
                    Text(
                        text = translatedUnavailable,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoRow(
                            label = translatedLatitude,
                            value = "%.5f".format(point.latitude)
                        )
                        InfoRow(
                            label = translatedLongitude,
                            value = "%.5f".format(point.longitude)
                        )
                        accuracyMeters?.let {
                            InfoRow(
                                label = translatedPrecision,
                                value = "± ${it.roundToInt()} m",
                                valueColor = Color(0xFF4CAF50)
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