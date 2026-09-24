package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Train
import androidx.compose.runtime.*
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage // AJOUT
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel // AJOUT

@Composable
fun StationTerDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage
) {
    var translatedTitle by remember { mutableStateOf("TER") }
    var translatedItemLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage, items) {
        translatedTitle = languageViewModel.translate("TER")

        val newLabels = items.associate { item ->
            item.key to StationTypeStyle.label(item.key, languageViewModel)
        }
        translatedItemLabels = newLabels
    }

    LayerSection(
        label = translatedTitle,
        icon = Icons.Default.Train,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemColor = { StationTypeStyle.color(it) },
        itemIcon = { StationTypeStyle.icon(it) },
        itemLabel = { key -> translatedItemLabels[key] ?: key }
    )
}