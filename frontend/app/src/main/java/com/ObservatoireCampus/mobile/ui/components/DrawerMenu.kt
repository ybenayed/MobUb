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
    onHistoryClick: () -> Unit = {},   // AJOUT : ouvre l'historique de recherche
    onAccountClick: () -> Unit = {},   // AJOUT : ouvre "Mon compte" (profil + mdp)
    onBackToMap: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var translatedMeteoLabel by remember { mutableStateOf("Météo") }
    var translatedItineraryLabel by remember { mutableStateOf("Itinéraire") }
    var translatedHistoryLabel by remember { mutableStateOf("Historique") }
    var translatedAccountLabel by remember { mutableStateOf("Mon compte") }
    var translatedLogoutLabel by remember { mutableStateOf("Déconnexion") }
    var translatedBackLabel by remember { mutableStateOf("Retour") }

    LaunchedEffect(currentLanguage) {
        translatedMeteoLabel = languageViewModel.translate("Météo")
        translatedItineraryLabel = languageViewModel.translate("Itinéraire")
        translatedHistoryLabel = languageViewModel.translate("Historique")
        translatedAccountLabel = languageViewModel.translate("Mon compte")
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
            MobUbTopBarBrand()
        }

        HorizontalDivider()

        // Conteneur deroulant : couches de la carte
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
            // METEO : simple raccourci de navigation, comme Itineraire/Historique
// --- SECTION BASSE (STATIQUE, TOUJOURS VISIBLE) ---
            HorizontalDivider()

            // METEO : deplacee au-dessus d'Itineraire/Historique comme demande
            DrawerNavigationRow(
                icon = Icons.Default.Cloud,
                label = translatedMeteoLabel,
                onClick = onWeatherClick
            )

            HorizontalDivider()

            DrawerNavigationRow(
                icon = Icons.Default.Directions,
                label = translatedItineraryLabel,
                onClick = onItineraryClick
            )

        HorizontalDivider()

        // AJOUT : Historique des recherches
        DrawerNavigationRow(
            icon = Icons.Default.History,
            label = translatedHistoryLabel,
            onClick = onHistoryClick
        )

        HorizontalDivider()

        LanguageDrawerSection(
            languageViewModel = languageViewModel,
            currentLanguage = currentLanguage,
            isTranslating = isTranslating,
            onLanguageSelected = onLanguageSelected
        )

        HorizontalDivider()

        // AJOUT : "Mon compte" (coordonnees utilisateur + changement de mot de passe)
        DrawerNavigationRow(
            icon = Icons.Default.AccountCircle,
            label = translatedAccountLabel,
            onClick = onAccountClick
        )

        HorizontalDivider()

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

/**
 * Ligne de navigation generique du tiroir (icone + label + fleche), utilisee
 * pour Itineraire / Historique / Meteo / Mon compte : evite de dupliquer le
 * meme Row 4 fois.
 */
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