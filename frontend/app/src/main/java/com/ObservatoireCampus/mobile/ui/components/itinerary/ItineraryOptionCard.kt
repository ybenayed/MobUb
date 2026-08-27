package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary

@Composable
fun ItineraryOptionCard(
    option: ItineraryOptionDto,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ObcampusPrimary.copy(alpha = 0.08f) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, ObcampusPrimary) else null,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = formatDuration(option.duration),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                option.legs.forEachIndexed { index, leg ->
                    Text(text = ItineraryModeStyle.emoji(leg.mode))
                    leg.routeName?.let { name ->
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                    }
                    if (index < option.legs.lastIndex) {
                        Text(text = "  →  ", color = Color.Gray)
                    }
                }
            }
        }
    }
}

private fun formatDuration(durationSeconds: Long): String {
    val minutes = durationSeconds / 60
    return if (minutes < 60) "$minutes min" else "${minutes / 60} h ${minutes % 60} min"
}