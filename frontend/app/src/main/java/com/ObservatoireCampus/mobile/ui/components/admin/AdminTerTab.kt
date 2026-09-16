package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings

@Composable
fun AdminTerTab(strings: InfraStrings) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(strings.comingSoon, color = Color.Gray)
    }
}