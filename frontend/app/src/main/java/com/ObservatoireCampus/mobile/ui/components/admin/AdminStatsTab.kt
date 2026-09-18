package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.admin.AdminModeCountDto
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.admin.AdminUserRepository
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminStatsViewModel
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminStatsViewModelFactory
import androidx.compose.ui.draw.clip
@Composable
fun AdminStatsTab(
    viewModel: AdminStatsViewModel = viewModel(
        factory = AdminStatsViewModelFactory(AdminUserRepository(RetrofitClient.adminUserApi))
    )
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && stats == null -> Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Chargement des statistiques...")
            }

            error != null && stats == null -> Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )

            stats != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { viewModel.loadStats() }) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir", tint = ObcampusPrimary)
                        }
                    }
                }

                // --- Cards de synthese ---
                StatSummaryCard(
                    icon = Icons.Default.Search,
                    label = "Recherches effectuées",
                    value = stats!!.totalSearches.toString()
                )
                StatSummaryCard(
                    icon = Icons.Default.People,
                    label = "Utilisateurs actifs (30 derniers jours)",
                    value = stats!!.activeUsersLast30Days.toString()
                )
                StatSummaryCard(
                    icon = Icons.Default.Eco,
                    label = "CO2 économisé cumulé",
                    value = "${formatCo2Kg(stats!!.totalCo2GramsSaved)} kg"
                )

                // --- Bar chart modes de transport ---
                if (stats!!.mostUsedModes.isNotEmpty()) {
                    Text(
                        "Modes de transport les plus utilisés",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = ObcampusPrimary
                    )
                    ModeBarChart(modes = stats!!.mostUsedModes)
                }
            }
        }
    }
}

@Composable
private fun StatSummaryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = ObcampusPrimary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ModeBarChart(modes: List<AdminModeCountDto>) {
    val sorted = modes.sortedByDescending { it.count }
    val maxCount = sorted.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sorted.forEach { modeCount ->
                ModeBarRow(modeCount = modeCount, maxCount = maxCount)
            }
        }
    }
}

@Composable
private fun ModeBarRow(modeCount: AdminModeCountDto, maxCount: Long) {
    val fraction = (modeCount.count.toFloat() / maxCount.toFloat()).coerceIn(0f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(modeCount.mode, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(modeCount.count.toString(), color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObcampusPrimary)
            )
        }
    }
}

/** Convertit des grammes en kg avec 1 decimale, ex: 12450.0 -> "12.5" */
private fun formatCo2Kg(grams: Double): String {
    val kg = grams / 1000.0
    return String.format("%.1f", kg)
}