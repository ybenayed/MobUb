package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryFilters
import com.ObservatoireCampus.mobile.model.search.ItinerarySortOption
import com.ObservatoireCampus.mobile.model.search.TransportModeUi
import com.ObservatoireCampus.mobile.ui.components.itinerary.filters.ModeFilterRow
import com.ObservatoireCampus.mobile.ui.components.itinerary.filters.SortFilterMenu
import com.ObservatoireCampus.mobile.ui.components.itinerary.filters.WheelchairFilterChip
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

/**
 * Assemble les 4 filtres, chacun dans son propre fichier sous filters/ :
 * ModeFilterRow, ItineraryTimeFilter, WheelchairFilterChip, SortFilterMenu.
 * Ce fichier ne contient plus AUCUNE logique d'affichage de chip : juste
 * la disposition verticale. Plus facile à lire, et à modifier filtre par filtre.
 */
@Composable
fun ItineraryFiltersBar(
    filters: ItineraryFilters,
    onModeToggle: (TransportModeUi) -> Unit,
    onTimeChange: (date: String?, time: String?, arriveBy: Boolean) -> Unit,
    onWheelchairToggle: () -> Unit,
    onSortChange: (ItinerarySortOption) -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedAccessibilityWarning by remember {
        mutableStateOf("Active le filtre PMR pour un score d'accessibilité pertinent.")
    }

    LaunchedEffect(currentLanguage) {
        translatedAccessibilityWarning = languageViewModel.translate("Active le filtre PMR pour un score d'accessibilité pertinent.")
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ModeFilterRow(
            selectedModes = filters.modes,
            onModeToggle = onModeToggle,
            languageViewModel = languageViewModel
        )

        ItineraryTimeFilter(
            date = filters.date,
            time = filters.time,
            arriveBy = filters.arriveBy,
            onChange = onTimeChange,
            languageViewModel = languageViewModel
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelchairFilterChip(
                isActive = filters.wheelchair,
                onToggle = onWheelchairToggle,
                languageViewModel = languageViewModel
            )
            SortFilterMenu(
                selected = filters.sortBy,
                onSortChange = onSortChange,
                languageViewModel = languageViewModel
            )
        }

        // Petit rappel contextuel : évite que l'utilisateur pense que le tri
        // "Le plus accessible" ne fonctionne pas alors qu'il n'a pas activé PMR.
        if (filters.sortBy == ItinerarySortOption.ACCESSIBILITY && !filters.wheelchair) {
            Text(
                text = translatedAccessibilityWarning,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}