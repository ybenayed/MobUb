package com.ObservatoireCampus.mobile.ui.components.itinerary.filters

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun WheelchairFilterChip(
    isActive: Boolean,
    onToggle: () -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedLabel by remember { mutableStateOf("PMR") }
    var translatedDesc by remember { mutableStateOf("Personne à mobilité réduite") }

    LaunchedEffect(currentLanguage) {
        translatedLabel = languageViewModel.translate("PMR")
        translatedDesc = languageViewModel.translate("Personne à mobilité réduite")
    }

    FilterChip(
        modifier = modifier,
        selected = isActive,
        onClick = onToggle,
        label = { Text(translatedLabel) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Accessible,
                contentDescription = translatedDesc,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ObcampusPrimary.copy(alpha = 0.15f),
            selectedLabelColor = ObcampusPrimary
        )
    )
}