package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun SearchBar(
    languageViewModel: LanguageViewModel,
    query: String,
    suggestions: List<SearchResultDto>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (SearchResultDto) -> Unit,
    onOpenItinerary: () -> Unit = {}, // <-- ouvre le panneau itinéraire (bottom sheet)
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var translatedPlaceholder by remember { mutableStateOf("Rechercher un lieu...") }
    var translatedSearchDesc by remember { mutableStateOf("Rechercher") }
    var translatedItineraryDesc by remember { mutableStateOf("Itinéraire") }

    LaunchedEffect(currentLanguage) {
        translatedPlaceholder = languageViewModel.translate("Rechercher un lieu...")
        translatedSearchDesc = languageViewModel.translate("Rechercher")
        translatedItineraryDesc = languageViewModel.translate("Itinéraire")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 3.dp,
            color = Color.White
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(translatedPlaceholder) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = translatedSearchDesc)
                },
                trailingIcon = {
                    IconButton(onClick = onOpenItinerary) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = translatedItineraryDesc)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ObcampusPrimary,
                    cursorColor = ObcampusPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Fenêtre d'autocomplétion (Dropdown) sous la barre de recherche
        if (suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                    itemsIndexed(suggestions) { index, suggestion ->
                        SearchSuggestionRow(
                            suggestion = suggestion,
                            onClick = { onSuggestionSelected(suggestion) }
                        )
                        if (index < suggestions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 52.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFFECECEC)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSuggestionRow(
    suggestion: SearchResultDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
            contentDescription = null,
            tint = ObcampusPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (suggestion.subtitle.isNotBlank()) {
                Text(
                    text = suggestion.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}