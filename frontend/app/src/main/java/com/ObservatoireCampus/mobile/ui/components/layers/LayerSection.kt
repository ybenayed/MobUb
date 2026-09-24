package com.ObservatoireCampus.mobile.ui.components.layers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingTypeStyle

@Composable
fun LayerSection(
    label: String,
    icon: ImageVector,
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit,
    itemColor: (String) -> Color = { ParkingTypeStyle.color(it) },
    itemIcon: (String) -> ImageVector = { ParkingTypeStyle.icon(it) },
    itemLabel: (String) -> String = { it }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandToggle)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    EyeToggleButton(active = masterActive, onClick = onMasterToggle, size = 34.dp)

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = label,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (items.isNotEmpty()) {
                    CountBadge(count = items.sumOf { it.count })
                }
            }

            AnimatedVisibility(
                visible = expanded && items.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    items.forEach { item ->
                        LayerSubItemRow(
                            item = item,
                            color = itemColor(item.key),
                            icon = itemIcon(item.key),
                            label = itemLabel(item.key),
                            onToggle = { onItemToggle(item.key) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerSubItemRow(
    item: LayerItemUiState,
    color: Color,
    icon: ImageVector,
    label: String,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = if (item.visible) 1f else 0.25f),
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (item.visible) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            CountBadge(count = item.count, subtle = true)
            Spacer(modifier = Modifier.width(12.dp))
            EyeToggleButton(active = item.visible, onClick = onToggle, size = 28.dp)
        }
    }
}

@Composable
private fun EyeToggleButton(active: Boolean, onClick: () -> Unit, size: androidx.compose.ui.unit.Dp) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (active) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (active) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "toggle",
                tint = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}

@Composable
private fun CountBadge(count: Long, subtle: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (subtle) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}