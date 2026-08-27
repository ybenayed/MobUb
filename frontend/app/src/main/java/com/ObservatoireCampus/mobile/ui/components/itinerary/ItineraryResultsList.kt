package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto

@Composable
fun ItineraryResultsList(
    options: List<ItineraryOptionDto>,
    selectedItinerary: ItineraryOptionDto?,
    onOptionSelected: (ItineraryOptionDto) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        when {
            isLoading -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Recherche d'itinéraires...")
            }
            errorMessage != null -> Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            options.isNotEmpty() -> LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(options) { option ->
                    ItineraryOptionCard(
                        option = option,
                        isSelected = option == selectedItinerary,
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}