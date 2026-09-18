package com.ObservatoireCampus.mobile.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.admin.AdminColorsTab
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLegendsScreen(
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(languageViewModel = languageViewModel, onMenuClick = onBack, isBackButton = true)
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Text(
                text = "Légendes",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = ObcampusPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            AdminColorsTab(strings = com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings())
        }
    }
}