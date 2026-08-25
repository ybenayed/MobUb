package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationTBDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationVDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationTerDrawerSection
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusSecondary
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

private data class DrawerOption(
    val id: String,
    val icon: ImageVector
)

private val drawerOptions = listOf(
    DrawerOption("bornes_electriques", Icons.Default.EvStation),
    DrawerOption("meteo", Icons.Default.Cloud)
)

@Composable
fun DrawerMenu(
    languageViewModel: LanguageViewModel,
    parkingLayers: List<LayerItemUiState>,
    parkingMasterActive: Boolean,
    parkingExpanded: Boolean,
    onParkingExpandToggle: () -> Unit,
    onParkingMasterToggle: () -> Unit,
    onParkingItemToggle: (String) -> Unit,
    stationTBLayers: List<LayerItemUiState>,
    stationTBMasterActive: Boolean,
    stationTBExpanded: Boolean,
    onStationTBExpandToggle: () -> Unit,
    onStationTBMasterToggle: () -> Unit,
    onStationTBItemToggle: (String) -> Unit,
    stationVLayers: List<LayerItemUiState>,
    stationVMasterActive: Boolean,
    stationVExpanded: Boolean,
    onStationVExpandToggle: () -> Unit,
    onStationVMasterToggle: () -> Unit,
    onStationVItemToggle: (String) -> Unit,
    stationTerLayers: List<LayerItemUiState>,
    stationTerMasterActive: Boolean,
    stationTerExpanded: Boolean,
    onStationTerExpandToggle: () -> Unit,
    onStationTerMasterToggle: () -> Unit,
    onStationTerItemToggle: (String) -> Unit,
    freeVehicleLayers: List<LayerItemUiState>,
    freeVehicleMasterActive: Boolean,
    freeVehicleExpanded: Boolean,
    onFreeVehicleExpandToggle: () -> Unit,
    onFreeVehicleMasterToggle: () -> Unit,
    onFreeVehicleItemToggle: (String) -> Unit,
    currentLanguage: AppLanguage,
    isTranslating: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    onWeatherClick: () -> Unit = {},
    onInternshipClick: () -> Unit = {}, // <-- Callback vers l'écran "À propos"
    onItineraryClick: () -> Unit = {}, // <-- Callback qui ouvre le panneau Itinéraire (bottom sheet)
    onOptionClick: (String, Boolean) -> Unit = { _, _ -> },
    onBackToMap: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var activeStates by remember {
        mutableStateOf(drawerOptions.associate { it.id to false })
    }

    var translatedBornesLabel by remember { mutableStateOf("Bornes électriques") }
    var translatedMeteoLabel by remember { mutableStateOf("Météo") }
    var translatedItineraryLabel by remember { mutableStateOf("Itinéraire") }
    var translatedLogoutLabel by remember { mutableStateOf("Déconnexion") }
    var translatedBackLabel by remember { mutableStateOf("Retour") }

    LaunchedEffect(currentLanguage) {
        translatedBornesLabel = languageViewModel.translate("Bornes électriques")
        translatedMeteoLabel = languageViewModel.translate("Météo")
        translatedItineraryLabel = languageViewModel.translate("Itinéraire")
        translatedLogoutLabel = languageViewModel.translate("Déconnexion")
        translatedBackLabel = languageViewModel.translate("Retour")
    }

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.69f)
            .background(Color.White)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackToMap) {
                Icon(Icons.Default.ArrowBack, contentDescription = translatedBackLabel)
            }
            Text(
                text = "OBCampus",
                style = MaterialTheme.typography.titleLarge,
                color = ObcampusPrimary
            )
        }

        HorizontalDivider()

        // Conteneur déroulant
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            // PARKING
            ParkingDrawerSection(
                items = parkingLayers,
                masterActive = parkingMasterActive,
                expanded = parkingExpanded,
                onExpandToggle = onParkingExpandToggle,
                onMasterToggle = onParkingMasterToggle,
                onItemToggle = onParkingItemToggle,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage
            )

            // BUS / TRAM
            StationTBDrawerSection(
                items = stationTBLayers,
                masterActive = stationTBMasterActive,
                expanded = stationTBExpanded,
                onExpandToggle = onStationTBExpandToggle,
                onMasterToggle = onStationTBMasterToggle,
                onItemToggle = onStationTBItemToggle,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage
            )

            // VELO
            StationVDrawerSection(
                items = stationVLayers,
                masterActive = stationVMasterActive,
                expanded = stationVExpanded,
                onExpandToggle = onStationVExpandToggle,
                onMasterToggle = onStationVMasterToggle,
                onItemToggle = onStationVItemToggle,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage
            )

            // TER
            StationTerDrawerSection(
                items = stationTerLayers,
                masterActive = stationTerMasterActive,
                expanded = stationTerExpanded,
                onExpandToggle = onStationTerExpandToggle,
                onMasterToggle = onStationTerMasterToggle,
                onItemToggle = onStationTerItemToggle,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage
            )

            // LIBRE-SERVICE
            FreeVehicleDrawerSection(
                items = freeVehicleLayers,
                masterActive = freeVehicleMasterActive,
                expanded = freeVehicleExpanded,
                onExpandToggle = onFreeVehicleExpandToggle,
                onMasterToggle = onFreeVehicleMasterToggle,
                onItemToggle = onFreeVehicleItemToggle,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage
            )

            // AUTRES OPTIONS
            drawerOptions.forEach { option ->
                val isActive = activeStates[option.id] == true
                val labelText = if (option.id == "bornes_electriques") translatedBornesLabel else translatedMeteoLabel

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isActive) ObcampusSecondary.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .clickable {
                            if (option.id == "meteo") {
                                onWeatherClick()
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = labelText,
                            tint = if (isActive) ObcampusPrimary else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = labelText,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (option.id != "meteo") {
                        IconButton(
                            onClick = {
                                val newState = !(activeStates[option.id] ?: false)
                                activeStates = activeStates.toMutableMap().apply {
                                    put(option.id, newState)
                                }
                                onOptionClick(option.id, newState)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isActive) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = "toggle"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(28.dp))
                    }
                }
            }
        }

        // --- SECTION BASSE (STATIQUE, TOUJOURS VISIBLE) ---
        HorizontalDivider()

        // 1. ITINÉRAIRE (ouvre le bottom sheet, même pattern que "À propos"/"Déconnexion")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItineraryClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Directions, contentDescription = translatedItineraryLabel, tint = ObcampusPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = translatedItineraryLabel, modifier = Modifier.weight(1f))
        }

        HorizontalDivider()

        // 2. SECTION LANGUE
        LanguageDrawerSection(
            languageViewModel = languageViewModel,
            currentLanguage = currentLanguage,
            isTranslating = isTranslating,
            onLanguageSelected = onLanguageSelected
        )

        HorizontalDivider()

        // 3. BOUTON "À PROPOS" (JUSTE AVANT LA DECONNEXION)
        AboutDrawerSection(
            languageViewModel = languageViewModel,
            currentLanguage = currentLanguage,
            onAboutClick = onInternshipClick
        )

        HorizontalDivider()

        // 4. BOUTON DE DECONNEXION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = translatedLogoutLabel, tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = translatedLogoutLabel, color = Color.Red, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = "logout", tint = Color.Gray)
        }
    }
}