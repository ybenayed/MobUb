package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun ItineraryResultsList(
    options: List<ItineraryOptionDto>,
    selectedItinerary: ItineraryOptionDto?,
    onOptionSelected: (ItineraryOptionDto) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedLoading by remember { mutableStateOf("Recherche d'itinéraires...") }
    var translatedError by remember { mutableStateOf(errorMessage) }

    LaunchedEffect(currentLanguage, errorMessage) {
        translatedLoading = languageViewModel.translate("Recherche d'itinéraires...")
        translatedError = errorMessage?.let { languageViewModel.translate(it) }
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        when {
            isLoading -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(translatedLoading)
            }
            translatedError != null -> Text(text = translatedError!!, color = MaterialTheme.colorScheme.error)
            options.isNotEmpty() -> LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(options) { option ->
                    ItineraryOptionCard(
                        option = option,
                        isSelected = option == selectedItinerary,
                        onClick = { onOptionSelected(option) },
                        languageViewModel = languageViewModel
                    )
                }
            }
        }
    }
}