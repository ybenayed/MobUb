package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.TransportModeUi

@Composable
fun ItineraryModeSelector(
    selectedModes: Set<TransportModeUi>,
    onModeToggle: (TransportModeUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransportModeUi.entries.forEach { mode ->
            FilterChip(
                selected = mode in selectedModes,
                onClick = { onModeToggle(mode) },
                label = { Text("${mode.emoji} ${mode.label}") }
            )
        }
    }
}