package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.runtime.Composable
import com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings

@Composable
fun AdminBusTab(strings: InfraStrings) {
    AdminStationTBTab(strings = strings, mode = "BUS", icon = Icons.Default.DirectionsBus)
}