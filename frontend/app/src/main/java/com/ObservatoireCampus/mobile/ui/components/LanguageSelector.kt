package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.WaypusInputBorder
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

/**
 * Bouton unique "langue" (drapeau + code + fleche). Au clic, deroule
 * la liste des 3 langues disponibles.
 */
@Composable
fun LanguageSelector(
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .border(width = 0.5.dp, color = WaypusInputBorder, shape = RoundedCornerShape(50))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FlagChip(language = currentLanguage)
            Text(
                text = currentLanguage.name,
                style = MaterialTheme.typography.labelMedium,
                color = WaypusTextDark
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Choisir la langue",
                tint = ObcampusPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppLanguage.values().forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FlagChip(language = lang)
                            Text(text = languageLabel(lang))
                        }
                    },
                    onClick = {
                        languageViewModel.setLanguage(lang)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun languageLabel(lang: AppLanguage): String = when (lang) {
    AppLanguage.FR -> "Francais"
    AppLanguage.EN -> "English"
    AppLanguage.AR -> "Arabe"
}

/**
 * Pastille visuelle par langue. FR/EN = approximation simple du drapeau.
 * AR = icone neutre (globe) en attendant que tu choisisses un drapeau
 * national precis (Tunisie, Maroc, Arabie Saoudite...) a mettre a la place.
 */
@Composable
private fun FlagChip(language: AppLanguage, size: Dp = 18.dp) {
    when (language) {
        AppLanguage.FR -> Row(
            modifier = Modifier
                .size(width = size, height = size * 0.7f)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF0055A4)))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFEF4135)))
        }
        AppLanguage.EN -> Box(
            modifier = Modifier
                .size(width = size, height = size * 0.7f)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF00247D)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.width(size * 0.5f).height(1.5.dp).background(Color.White))
        }
        AppLanguage.AR -> Box(
            modifier = Modifier
                .size(width = size, height = size * 0.7f)
                .clip(RoundedCornerShape(2.dp))
                .border(width = 0.5.dp, color = WaypusInputBorder, shape = RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = ObcampusPrimary,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}