package com.ObservatoireCampus.mobile.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ObservatoireCampus.mobile.ui.components.TopBar
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
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Gestion des legendes — a venir")
        }
    }
}