package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.model.BatimentDto
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun CampusLegend(
    batimentList: List<BatimentDto>,
    legendList: List<InstitutionColorDto>,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    // Ne filtrer que les institutions actives dans la liste actuelle des bâtiments
    val activeLegend = remember(batimentList, legendList) {
        // Normalisation en minuscules et suppression des espaces superflus
        val presentApps = batimentList
            .mapNotNull { it.appartenance?.trim()?.lowercase() }
            .toSet()

        legendList.filter { legend ->
            presentApps.contains(legend.institution.trim().lowercase())
        }
    }
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .heightIn(max = 200.dp)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            activeLegend.forEach { item ->
                var translatedLabel by remember(item.institution, currentLanguage) {
                    mutableStateOf(item.institution)
                }
                LaunchedEffect(item.institution, currentLanguage) {
                    translatedLabel = languageViewModel.translate(item.institution)
                }

                val itemColor = try {
                    Color(android.graphics.Color.parseColor(item.color))
                } catch (_: Exception) {
                    Color.Gray
                }

                LegendRowItem(color = itemColor, label = translatedLabel)
            }
        }
    }
}

@Composable
private fun LegendRowItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Color.White, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2C3E50),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}