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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.zIndex
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.WaypusInputBorder
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun LanguageSelector(
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Le zIndex(10f) force Compose à afficher ce bloc TOUJOURS au-dessus de la carte OSM
    Box(modifier = modifier.zIndex(10f)) {
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(width = 0.5.dp, color = WaypusInputBorder, shape = RoundedCornerShape(50))
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .zIndex(11f) // Force la liste déroulante à passer par-dessus l'en-tête
        ) {
            AppLanguage.values().forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FlagChip(language = lang)
                            Text(
                                text = languageLabel(lang),
                                color = WaypusTextDark
                            )
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
    AppLanguage.FR -> "Français"
    AppLanguage.EN -> "English"
    AppLanguage.AR -> "Arabe"
}

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