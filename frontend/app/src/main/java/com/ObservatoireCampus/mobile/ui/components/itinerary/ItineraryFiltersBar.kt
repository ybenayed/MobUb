package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryFilters
import com.ObservatoireCampus.mobile.model.search.ItinerarySortOption
import com.ObservatoireCampus.mobile.model.search.TransportModeUi
import com.ObservatoireCampus.mobile.ui.components.itinerary.filters.ModeFilterRow
import com.ObservatoireCampus.mobile.ui.components.itinerary.filters.SortFilterMenu
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

/**
 * Assemble les filtres : ModeFilterRow, ItineraryTimeFilter, SortFilterMenu.
 * Le filtre PMR (WheelchairFilterChip) a ete retire, ainsi que le message
 * d'avertissement associe.
 */
@Composable
fun ItineraryFiltersBar(
    filters: ItineraryFilters,
    onModeToggle: (TransportModeUi) -> Unit,
    onTimeChange: (date: String?, time: String?, arriveBy: Boolean) -> Unit,
    onSortChange: (ItinerarySortOption) -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
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
            horizontalArrangement = Arrangement.End
        ) {
            SortFilterMenu(
                selected = filters.sortBy,
                onSortChange = onSortChange,
                languageViewModel = languageViewModel
            )
        }
    }
}