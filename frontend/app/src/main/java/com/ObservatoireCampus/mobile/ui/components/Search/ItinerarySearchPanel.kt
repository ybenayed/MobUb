package com.ObservatoireCampus.mobile.ui.components.Search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryFilters
import com.ObservatoireCampus.mobile.model.search.ItinerarySortOption
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.model.search.TransportModeUi
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryFiltersBar
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun ItinerarySearchPanel(
    originQuery: String,
    originSuggestions: List<SearchResultDto>,
    onOriginQueryChange: (String) -> Unit,
    onOriginSuggestionSelected: (SearchResultDto) -> Unit,
    onUseMyLocationAsOrigin: () -> Unit,
    destinationQuery: String,
    destinationSuggestions: List<SearchResultDto>,
    onDestinationQueryChange: (String) -> Unit,
    onDestinationSuggestionSelected: (SearchResultDto) -> Unit,
    onUseMyLocationAsDestination: () -> Unit,
    filters: ItineraryFilters,
    onModeToggle: (TransportModeUi) -> Unit,
    onTimeChange: (date: String?, time: String?, arriveBy: Boolean) -> Unit,
    onWheelchairToggle: () -> Unit,
    onSortChange: (ItinerarySortOption) -> Unit,
    canSearch: Boolean,
    onSearchClick: () -> Unit,
    onResetClick: () -> Unit, // <-- Callback de réinitialisation
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedTitle by remember { mutableStateOf("Itinéraire") }
    var translatedOriginPlaceholder by remember { mutableStateOf("Position initiale") }
    var translatedDestPlaceholder by remember { mutableStateOf("Destination") }
    var translatedMyLocationDesc by remember { mutableStateOf("Utiliser ma position") }
    var translatedSearchButton by remember { mutableStateOf("Rechercher") }
    var translatedResetDesc by remember { mutableStateOf("Réinitialiser les champs") }

    LaunchedEffect(currentLanguage) {
        translatedTitle = languageViewModel.translate("Itinéraire")
        translatedOriginPlaceholder = languageViewModel.translate("Position initiale")
        translatedDestPlaceholder = languageViewModel.translate("Destination")
        translatedMyLocationDesc = languageViewModel.translate("Utiliser ma position")
        translatedSearchButton = languageViewModel.translate("Rechercher")
        translatedResetDesc = languageViewModel.translate("Réinitialiser les champs")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = translatedTitle,
            style = MaterialTheme.typography.titleLarge,
            color = ObcampusPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        AutocompleteField(
            value = originQuery,
            suggestions = originSuggestions,
            onValueChange = onOriginQueryChange,
            onSuggestionSelected = onOriginSuggestionSelected,
            placeholder = translatedOriginPlaceholder,
            trailingIcon = {
                IconButton(onClick = onUseMyLocationAsOrigin) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = translatedMyLocationDesc,
                        tint = ObcampusPrimary
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AutocompleteField(
            value = destinationQuery,
            suggestions = destinationSuggestions,
            onValueChange = onDestinationQueryChange,
            onSuggestionSelected = onDestinationSuggestionSelected,
            placeholder = translatedDestPlaceholder,
            trailingIcon = {
                IconButton(onClick = onUseMyLocationAsDestination) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = translatedMyLocationDesc,
                        tint = ObcampusPrimary
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ItineraryFiltersBar(
            filters = filters,
            onModeToggle = onModeToggle,
            onTimeChange = onTimeChange,
            onWheelchairToggle = onWheelchairToggle,
            onSortChange = onSortChange,
            languageViewModel = languageViewModel,
            modifier = Modifier.padding(horizontal = 0.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Rangée avec le bouton "Rechercher" et le bouton icône "Réinitialiser"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onSearchClick,
                enabled = canSearch,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ObcampusPrimary)
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(translatedSearchButton)
            }

            OutlinedIconButton(
                onClick = onResetClick,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = translatedResetDesc
                )
            }
        }
    }
}