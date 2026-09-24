package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusTextWhite
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel


@Composable
fun TopBar(
    languageViewModel: LanguageViewModel,
    onMenuClick: () -> Unit,
    isBackButton: Boolean = false
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedMenuDesc by remember { mutableStateOf("Menu") }
    var translatedBackDesc by remember { mutableStateOf("Retour") }

    LaunchedEffect(currentLanguage) {
        translatedMenuDesc = languageViewModel.translate("Menu")
        translatedBackDesc = languageViewModel.translate("Retour")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObcampusPrimary)
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = if (isBackButton) Icons.Default.ArrowBack else Icons.Default.Menu,
                contentDescription = if (isBackButton) translatedBackDesc else translatedMenuDesc,
                tint = ObcampusTextWhite
            )
        }
        MobUbTopBarBrand(
            onColoredBackground = true,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}