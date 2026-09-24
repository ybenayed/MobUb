package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CurrentDateTimeHeader(
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(60_000L)
        }
    }

    val currentLocale = when (currentLanguage) {
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.AR -> Locale.forLanguageTag("ar")
        else -> Locale.FRENCH
    }

    val dayLabel = now.dayOfWeek.getDisplayName(TextStyle.FULL, currentLocale)
        .replaceFirstChar { it.uppercase() }
    val dateFormatted = now.format(DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale))
    val timeFormatted = now.format(DateTimeFormatter.ofPattern("HH:mm"))

    Text(
        text = "$dayLabel $dateFormatted · $timeFormatted",
        modifier = modifier.padding(vertical = 4.dp),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
}