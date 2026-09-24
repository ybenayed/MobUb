package com.ObservatoireCampus.mobile.ui.components.layers.parking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.runtime.*
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun ParkingDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage
) {
    var translatedTitle by remember { mutableStateOf("Parking") }

    // Map pour stocker la traduction de chaque type de parking
    var translatedItemLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage, items) {
        translatedTitle = languageViewModel.translate("Parking")

        val newLabels = items.associate { item ->
            item.key to ParkingTypeStyle.label(item.key, languageViewModel)
        }
        translatedItemLabels = newLabels
    }

    LayerSection(
        label = translatedTitle,
        icon = Icons.Default.LocalParking,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemLabel = { key -> translatedItemLabels[key] ?: key }
    )
}