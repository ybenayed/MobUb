package com.ObservatoireCampus.mobile.ui.components.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.CURRENT_LOCATION_MARKER
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryFormat
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryModeStyle

@Composable
fun SearchHistoryCard(
    item: SearchHistoryDto,
    isDeleting: Boolean,
    onDelete: () -> Unit,
    myLocationLabel: String = "Ma position",   // <-- traduit par l'ecran parent, jamais stocke en base
    deleteLabel: String = "Supprimer",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Bloc origine / destination empile verticalement (comme Google Maps) :
            // regle definitivement le mauvais rendu quand un nom est trop long.
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                RouteEndpointRow(
                    icon = Icons.Default.TripOrigin,
                    iconTint = Color(0xFF16A34A),
                    rawName = item.originName,
                    fallback = "Origine",
                    myLocationLabel = myLocationLabel
                )
                RouteEndpointRow(
                    icon = Icons.Default.LocationOn,
                    iconTint = Color(0xFFDC2626),
                    rawName = item.destinationName,
                    fallback = "Destination",
                    myLocationLabel = myLocationLabel
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                item.legs.forEachIndexed { index, leg ->
                    Text(text = ItineraryModeStyle.emoji(leg.mode))
                    if (index < item.legs.lastIndex) {
                        Text(text = " → ", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEFEFEF))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(ItineraryFormat.duration(item.duration), style = MaterialTheme.typography.labelSmall)
                Text(ItineraryFormat.transfers(item.numberOfTransfers), style = MaterialTheme.typography.labelSmall)
                item.co2Grams?.let {
                    Text(ItineraryFormat.co2(it), style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = deleteLabel,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteEndpointRow(
    icon: ImageVector,
    iconTint: Color,
    rawName: String?,
    fallback: String,
    myLocationLabel: String
) {
    val isMyLocation = isCurrentLocationLabel(rawName)
    val displayText = when {
        isMyLocation -> myLocationLabel
        rawName.isNullOrBlank() -> fallback
        else -> rawName
    }

    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = if (isMyLocation) Icons.Default.MyLocation else icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Reconnait un nom correspondant a "ma position", que ce soit le nouveau
 * marqueur technique (CURRENT_LOCATION_MARKER) ou un ancien texte deja
 * enregistre en base avant la correction (traduit en dur, potentiellement
 * dans une autre langue que celle affichee actuellement).
 */
private fun isCurrentLocationLabel(raw: String?): Boolean {
    if (raw == null) return false
    if (raw == CURRENT_LOCATION_MARKER) return true
    val known = setOf("ma position", "my location", "my position", "mi ubicación", "mi posición")
    return raw.trim().lowercase() in known
}