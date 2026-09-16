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

@Composable
fun DrawerMenu(
    languageViewModel: LanguageViewModel,
    isAdmin: Boolean = false,   // AJOUT : bascule le menu USER / ADMIN
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
    onInternshipClick: () -> Unit = {},
    onItineraryClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    // AJOUT : actions reservees a l'admin
    onUserManagementClick: () -> Unit = {},
    onInfrastructureClick: () -> Unit = {},
    onLegendsClick: () -> Unit = {},
    onBackToMap: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var translatedMeteoLabel by remember { mutableStateOf("Météo") }
    var translatedItineraryLabel by remember { mutableStateOf("Itinéraire") }
    var translatedHistoryLabel by remember { mutableStateOf("Historique") }
    var translatedAccountLabel by remember { mutableStateOf("Mon compte") }
    var translatedLogoutLabel by remember { mutableStateOf("Déconnexion") }
    var translatedBackLabel by remember { mutableStateOf("Retour") }

    // AJOUT : libelles admin
    var translatedUserManagementLabel by remember { mutableStateOf("Gestion des utilisateurs") }
    var translatedInfrastructureLabel by remember { mutableStateOf("Infrastructures") }
    var translatedLegendsLabel by remember { mutableStateOf("Légendes") }

    LaunchedEffect(currentLanguage) {
        translatedMeteoLabel = languageViewModel.translate("Météo")
        translatedItineraryLabel = languageViewModel.translate("Itinéraire")
        translatedHistoryLabel = languageViewModel.translate("Historique")
        translatedAccountLabel = languageViewModel.translate("Mon compte")
        translatedLogoutLabel = languageViewModel.translate("Déconnexion")
        translatedBackLabel = languageViewModel.translate("Retour")
        translatedUserManagementLabel = languageViewModel.translate("Gestion des utilisateurs")
        translatedInfrastructureLabel = languageViewModel.translate("Infrastructures")
        translatedLegendsLabel = languageViewModel.translate("Légendes")
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
            MobUbTopBarBrand()
        }

        HorizontalDivider()

        // Conteneur deroulant : couches de la carte (communes admin + user)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
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
        }

        // --- SECTION BASSE (STATIQUE, TOUJOURS VISIBLE) ---
        HorizontalDivider()

        // METEO : commune admin + user
        DrawerNavigationRow(
            icon = Icons.Default.Cloud,
            label = translatedMeteoLabel,
            onClick = onWeatherClick
        )

        HorizontalDivider()

        if (isAdmin) {
            // --- MENU ADMIN ---
            DrawerNavigationRow(
                icon = Icons.Default.ManageAccounts,
                label = translatedUserManagementLabel,
                onClick = onUserManagementClick
            )

            HorizontalDivider()

            DrawerNavigationRow(
                icon = Icons.Default.Build,
                label = translatedInfrastructureLabel,
                onClick = onInfrastructureClick
            )

            HorizontalDivider()

            DrawerNavigationRow(
                icon = Icons.Default.Palette,
                label = translatedLegendsLabel,
                onClick = onLegendsClick
            )
        } else {
            // --- MENU UTILISATEUR (inchange) ---
            DrawerNavigationRow(
                icon = Icons.Default.Directions,
                label = translatedItineraryLabel,
                onClick = onItineraryClick
            )

            HorizontalDivider()

            DrawerNavigationRow(
                icon = Icons.Default.History,
                label = translatedHistoryLabel,
                onClick = onHistoryClick
            )
        }

        HorizontalDivider()

        LanguageDrawerSection(
            languageViewModel = languageViewModel,
            currentLanguage = currentLanguage,
            isTranslating = isTranslating,
            onLanguageSelected = onLanguageSelected
        )

        HorizontalDivider()

        if (!isAdmin) {
            // "Mon compte" reserve a l'utilisateur normal (l'admin n'a pas de profil a editer ici)
            DrawerNavigationRow(
                icon = Icons.Default.AccountCircle,
                label = translatedAccountLabel,
                onClick = onAccountClick
            )

            HorizontalDivider()
        }

        AboutDrawerSection(
            languageViewModel = languageViewModel,
            currentLanguage = currentLanguage,
            onAboutClick = onInternshipClick
        )

        HorizontalDivider()

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

@Composable
private fun DrawerNavigationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = ObcampusPrimary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}