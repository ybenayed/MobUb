package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusSecondary
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

private fun AppLanguage.label(): String = when (this) {
    AppLanguage.FR -> "Français"
    AppLanguage.EN -> "English"
    AppLanguage.AR -> "العربية (Arabe)"
}


@Composable
fun LanguageDrawerSection(
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    isTranslating: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // État pour stocker la traduction du mot "Langue"
    var translatedLabel by remember { mutableStateOf("Langue") }

    // Déclenche la traduction à chaque changement de langue
    LaunchedEffect(currentLanguage) {
        translatedLabel = languageViewModel.translate("Langue")
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !isTranslating) { expanded = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = translatedLabel,
                        tint = ObcampusPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(text = translatedLabel, modifier = Modifier.weight(1f))

                    Text(
                        text = currentLanguage.label(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (isTranslating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Ouvrir")
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppLanguage.values().forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang.label()) },
                        onClick = {
                            expanded = false
                            onLanguageSelected(lang)
                        },
                        trailingIcon = {
                            if (lang == currentLanguage) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ObcampusSecondary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}