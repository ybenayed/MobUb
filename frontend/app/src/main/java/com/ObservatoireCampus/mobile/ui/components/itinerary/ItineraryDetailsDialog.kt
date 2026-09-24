package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel


@Composable
fun ItineraryDetailsDialog(
    option: ItineraryOptionDto,
    onDismiss: () -> Unit,
    onViewOnMap: () -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedTitle by remember { mutableStateOf("Détails du trajet") }
    var translatedDeparture by remember { mutableStateOf("Départ") }
    var translatedArrival by remember { mutableStateOf("Arrivée estimée") }
    var translatedViewOnMap by remember { mutableStateOf("Voir sur la carte") }
    var translatedClose by remember { mutableStateOf("Fermer") }

    LaunchedEffect(currentLanguage) {
        translatedTitle = languageViewModel.translate("Détails du trajet")
        translatedDeparture = languageViewModel.translate("Départ")
        translatedArrival = languageViewModel.translate("Arrivée estimée")
        translatedViewOnMap = languageViewModel.translate("Voir sur la carte")
        translatedClose = languageViewModel.translate("Fermer")
    }

    val departureTime = option.legs.firstOrNull()?.startTime ?: 0L
    val arrivalTime = option.legs.lastOrNull()?.endTime ?: 0L

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(translatedTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(translatedDeparture)
                    Text(ItineraryFormat.clockTime(departureTime), fontWeight = FontWeight.SemiBold)
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(translatedArrival)
                    Text(ItineraryFormat.clockTime(arrivalTime), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(ItineraryFormat.duration(option.duration), style = MaterialTheme.typography.labelMedium)
            }
        },
        confirmButton = {
            TextButton(onClick = onViewOnMap) {
                Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text(translatedViewOnMap, color = ObcampusPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(translatedClose) }
        }
    )
}