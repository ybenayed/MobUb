package com.ObservatoireCampus.mobile.ui.components.itinerary.filters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItinerarySortOption
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterMenu(
    selected: ItinerarySortOption,
    onSortChange: (ItinerarySortOption) -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    var translatedSelectedLabel by remember { mutableStateOf(selected.label) }
    var translatedSortDesc by remember { mutableStateOf("Trier") }
    var translatedOptions by remember { mutableStateOf<Map<ItinerarySortOption, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage, selected) {
        translatedSelectedLabel = languageViewModel.translate(selected.label)
        translatedSortDesc = languageViewModel.translate("Trier")
        translatedOptions = ItinerarySortOption.entries.associateWith { option ->
            languageViewModel.translate(option.label)
        }
    }

    Box(modifier = modifier) {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(translatedSelectedLabel) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = translatedSortDesc,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ItinerarySortOption.entries.forEach { option ->
                val optionLabel = translatedOptions[option] ?: option.label
                DropdownMenuItem(
                    text = { Text(optionLabel) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}