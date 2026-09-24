package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ObservatoireCampus.mobile.model.weather.CurrentWeatherDto
import com.ObservatoireCampus.mobile.repository.weather.WeatherRepository

@Composable
fun CurrentWeatherBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    repository: WeatherRepository = remember { WeatherRepository() }
) {
    var current by remember { mutableStateOf<CurrentWeatherDto?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        current = try {
            repository.getCurrent()
        } catch (e: Exception) {
            null
        }
        loading = false
    }

    Surface(
        modifier = modifier.clickable(enabled = current != null) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                current == null -> Text("Météo indisponible", fontSize = 12.sp, color = Color.Gray)
                else -> {
                    AsyncImage(
                        model = current!!.icon,
                        contentDescription = current!!.description,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = current!!.temperature?.let { "${it.toInt()}°C" } ?: "--",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}