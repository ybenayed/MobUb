package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import kotlinx.coroutines.launch

@Composable
fun HourlyWeatherScale(
    points: List<HourlyWeatherPointDto>,
    selectedIndex: Int,
    onHourSelected: (Int) -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var textPrev by remember { mutableStateOf("Heures précédentes") }
    var textNext by remember { mutableStateOf("Heures suivantes") }

    LaunchedEffect(currentLanguage) {
        textPrev = languageViewModel.translate("Heures précédentes")
        textNext = languageViewModel.translate("Heures suivantes")
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            listState.scrollToItem((selectedIndex - 2).coerceAtLeast(0))
        }
    }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            scope.launch {
                listState.animateScrollToItem((listState.firstVisibleItemIndex - 3).coerceAtLeast(0))
            }
        }) {
            Icon(Icons.Default.ChevronLeft, contentDescription = textPrev, tint = Color.White)
        }

        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(points.size) { index ->
                val point = points[index]
                val isSelected = index == selectedIndex

                // Traduction à la volée de la description météo envoyée à l'image
                var translatedDesc by remember { mutableStateOf(point.description ?: "") }
                LaunchedEffect(currentLanguage, point.description) {
                    translatedDesc = point.description?.let { languageViewModel.translate(it) } ?: ""
                }

                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { onHourSelected(index) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = point.time.takeLast(5), color = Color.White, fontSize = 11.sp)
                    AsyncImage(
                        model = point.icon,
                        contentDescription = translatedDesc,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = point.temperature?.let { "${it.toInt()}°" } ?: "--",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        IconButton(onClick = {
            scope.launch {
                val maxIndex = (points.size - 1).coerceAtLeast(0)
                listState.animateScrollToItem((listState.firstVisibleItemIndex + 3).coerceAtMost(maxIndex))
            }
        }) {
            Icon(Icons.Default.ChevronRight, contentDescription = textNext, tint = Color.White)
        }
    }
}