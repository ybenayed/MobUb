package com.ObservatoireCampus.mobile.ui.components.Search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary

/**
 * Contenu du ModalBottomSheet "Itinéraire" : 2 champs (origine / destination),
 * chacun avec autocomplétion + bouton "ma position" 🎯, et le bouton "Rechercher".
 * Réutilise AutocompleteField (extrait de SearchBar) pour ne pas dupliquer
 * la logique de dropdown.
 */
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
    canSearch: Boolean,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Itinéraire",
            style = MaterialTheme.typography.titleLarge,
            color = ObcampusPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        AutocompleteField(
            value = originQuery,
            suggestions = originSuggestions,
            onValueChange = onOriginQueryChange,
            onSuggestionSelected = onOriginSuggestionSelected,
            placeholder = "Position initiale",
            trailingIcon = {
                IconButton(onClick = onUseMyLocationAsOrigin) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Utiliser ma position",
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
            placeholder = "Destination",
            trailingIcon = {
                IconButton(onClick = onUseMyLocationAsDestination) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Utiliser ma position",
                        tint = ObcampusPrimary
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSearchClick,
            enabled = canSearch,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ObcampusPrimary)
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Rechercher")
        }
    }
}