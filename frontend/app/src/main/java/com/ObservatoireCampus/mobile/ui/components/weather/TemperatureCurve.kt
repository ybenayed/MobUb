package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun TemperatureCurve(
    points: List<HourlyWeatherPointDto>,
    selectedIndex: Int,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    var showInfo by remember { mutableStateOf(false) }

    var textTitle by remember { mutableStateOf("Courbe de température") }
    var textBody by remember { mutableStateOf("") }

    LaunchedEffect(currentLanguage) {
        textTitle = languageViewModel.translate("Courbe de température")
        textBody = languageViewModel.translate(
            "Cette courbe représente l'évolution de la température heure par heure " +
                    "pour la journée sélectionnée. Le point orange correspond à l'heure " +
                    "actuellement choisie sur l'échelle horaire ci-dessous."
        )
    }

    val temps = points.mapNotNull { it.temperature }
    if (temps.isEmpty()) return
    val minT = temps.min()
    val maxT = temps.max()
    val range = (maxT - minT).let { if (it > 0.01) it else 1.0 }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showInfo = true }
                .padding(vertical = 8.dp)
        ) {
            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
            val path = Path()
            points.forEachIndexed { index, point ->
                val t = point.temperature ?: return@forEachIndexed
                val x = index * stepX
                val y = size.height - ((t - minT) / range).toFloat() * size.height
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path = path, color = Color.White, style = Stroke(width = 5f, cap = StrokeCap.Round))

            if (selectedIndex in points.indices) {
                points[selectedIndex].temperature?.let { t ->
                    val x = selectedIndex * stepX
                    val y = size.height - ((t - minT) / range).toFloat() * size.height
                    drawCircle(color = Color.White, radius = 11f, center = Offset(x, y))
                    drawCircle(color = Color(0xFFFFA000), radius = 7f, center = Offset(x, y))
                }
            }
        }
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) { Text("OK") }
            },
            title = { Text(textTitle) },
            text = { Text(textBody) }
        )
    }
}