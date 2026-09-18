package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tram
import androidx.compose.runtime.Composable
import com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings

@Composable
fun AdminTramTab(strings: InfraStrings) {
    AdminStationTBTab(strings = strings, mode = "TRAM", icon = Icons.Default.Tram)
}