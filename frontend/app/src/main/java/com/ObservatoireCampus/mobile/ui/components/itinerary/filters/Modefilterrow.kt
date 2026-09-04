package com.ObservatoireCampus.mobile.ui.components.itinerary.filters

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.TransportModeUi
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun ModeFilterRow(
    selectedModes: Set<TransportModeUi>,
    onModeToggle: (TransportModeUi) -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var translatedModeLabels by remember { mutableStateOf<Map<TransportModeUi, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage) {
        translatedModeLabels = TransportModeUi.entries.associateWith { mode ->
            languageViewModel.translate(mode.label)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransportModeUi.entries.forEach { mode ->
            val isSelected = mode in selectedModes
            val label = translatedModeLabels[mode] ?: mode.label

            FilterChip(
                selected = isSelected,
                onClick = { onModeToggle(mode) },
                label = { Text("${mode.emoji} $label") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ObcampusPrimary.copy(alpha = 0.15f),
                    selectedLabelColor = ObcampusPrimary
                )
            )
        }
    }
}