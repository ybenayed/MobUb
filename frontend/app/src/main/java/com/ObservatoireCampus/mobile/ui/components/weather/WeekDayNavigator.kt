package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekDayNavigator(
    selectedDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    minDate: LocalDate = LocalDate.now().minusDays(7),
    maxDate: LocalDate = LocalDate.now().plusDays(6)
) {
    // Détermination de la locale système
    val currentLocale = when (currentLanguage) {
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.AR -> Locale.forLanguageTag("ar")
        else -> Locale.FRENCH
    }

    var textPrev by remember { mutableStateOf("Jour précédent") }
    var textNext by remember { mutableStateOf("Jour suivant") }

    LaunchedEffect(currentLanguage) {
        textPrev = languageViewModel.translate("Jour précédent")
        textNext = languageViewModel.translate("Jour suivant")
    }

    // Formatage dynamique selon la locale
    val dayOfWeekLabel = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, currentLocale)
        .replaceFirstChar { it.uppercase() }
    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", currentLocale))
    val label = "$dayOfWeekLabel $formattedDate"

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious, enabled = !selectedDate.isBefore(minDate.plusDays(1))) {
            Icon(Icons.Default.ChevronLeft, contentDescription = textPrev, tint = Color.White)
        }
        Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        IconButton(onClick = onNext, enabled = !selectedDate.isAfter(maxDate.minusDays(1))) {
            Icon(Icons.Default.ChevronRight, contentDescription = textNext, tint = Color.White)
        }
    }
}