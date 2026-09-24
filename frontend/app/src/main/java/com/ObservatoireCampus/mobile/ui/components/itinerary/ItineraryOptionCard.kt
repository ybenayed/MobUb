package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun ItineraryOptionCard(
    option: ItineraryOptionDto,
    isSelected: Boolean,
    onClick: () -> Unit,
    isSaved: Boolean,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedProfileLabel by remember { mutableStateOf(option.profileLabel) }
    var translatedSave by remember { mutableStateOf("Enregistrer") }
    var translatedSaved by remember { mutableStateOf("Enregistré") }

    LaunchedEffect(currentLanguage, option.profileLabel) {
        translatedProfileLabel = option.profileLabel?.let { languageViewModel.translate(it) }
        translatedSave = languageViewModel.translate("Enregistrer")
        translatedSaved = languageViewModel.translate("Enregistré")
    }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ItineraryFormat.duration(option.duration),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                translatedProfileLabel?.let { label ->
                    AssistChip(
                        onClick = {},
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = ObcampusPrimary.copy(alpha = 0.1f),
                            labelColor = ObcampusPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                option.legs.forEachIndexed { index, leg ->
                    Text(text = ItineraryModeStyle.emojiForLeg(leg))
                    leg.routeName?.let { name ->
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                    }
                    if (index < option.legs.lastIndex) {
                        Text(text = "  →  ", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEFEFEF))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MetricBadge(
                    icon = Icons.Default.DirectionsWalk,
                    text = ItineraryFormat.walkDistance(option.walkDistance)
                )
                MetricBadge(
                    icon = Icons.Default.CompareArrows,
                    text = ItineraryFormat.transfers(option.transfers)
                )
                MetricBadge(
                    icon = Icons.Default.Park,
                    text = ItineraryFormat.co2(option.co2Grams),
                    tint = co2Tint(option.co2Grams)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    isSaving -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    isSaved -> TextButton(onClick = {}, enabled = false) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = ObcampusPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(translatedSaved, color = ObcampusPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                    else -> TextButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(translatedSave, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color = Color.Gray
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
    }
}

private fun co2Tint(grams: Double): Color = when {
    grams <= 0.0 -> Color(0xFF16A34A)
    grams < 200 -> Color(0xFF65A30D)
    grams < 800 -> Color(0xFFF59E0B)
    else -> Color(0xFFDC2626)
}