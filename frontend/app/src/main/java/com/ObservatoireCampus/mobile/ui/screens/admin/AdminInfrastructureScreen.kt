package com.ObservatoireCampus.mobile.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.admin.AdminBusTab
import com.ObservatoireCampus.mobile.ui.components.admin.AdminParkingTab
import com.ObservatoireCampus.mobile.ui.components.admin.AdminStationVTab
import com.ObservatoireCampus.mobile.ui.components.admin.AdminTerTab
import com.ObservatoireCampus.mobile.ui.components.admin.AdminTramTab
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

data class InfraStrings(
    val title: String = "Infrastructures",
    val tabVelo: String = "Vélo",
    val tabBus: String = "Bus",
    val tabTram: String = "Tram",
    val tabTer: String = "TER",
    val tabParking: String = "Parking",
    val loading: String = "Chargement...",
    val empty: String = "Aucune station pour le moment.",
    val comingSoon: String = "Bientôt disponible",
    val addTitle: String = "Ajouter une station",
    val editTitle: String = "Modifier la station",
    val fieldStationId: String = "Identifiant station",
    val fieldNom: String = "Nom",
    val fieldAdresse: String = "Adresse",
    val fieldCapacite: String = "Capacité",
    val fieldLatitude: String = "Latitude",
    val fieldLongitude: String = "Longitude",
    val save: String = "Enregistrer",
    val cancel: String = "Annuler",
    val confirmDeleteTitle: String = "Supprimer",
    val confirmDeleteMessage: String = "Cette action est irréversible.",
    val confirmDelete: String = "Supprimer",
    val capacitePlaces: String = "places",
    val searchHint: String = "Rechercher une station..."
)

suspend fun LanguageViewModel.translateInfraStrings(): InfraStrings {
    val b = InfraStrings()
    return InfraStrings(
        title = translate(b.title),
        tabVelo = translate(b.tabVelo),
        tabBus = translate(b.tabBus),
        tabTram = translate(b.tabTram),
        tabTer = translate(b.tabTer),
        tabParking = translate(b.tabParking),
        loading = translate(b.loading),
        empty = translate(b.empty),
        comingSoon = translate(b.comingSoon),
        addTitle = translate(b.addTitle),
        editTitle = translate(b.editTitle),
        fieldStationId = translate(b.fieldStationId),
        fieldNom = translate(b.fieldNom),
        fieldAdresse = translate(b.fieldAdresse),
        fieldCapacite = translate(b.fieldCapacite),
        fieldLatitude = translate(b.fieldLatitude),
        fieldLongitude = translate(b.fieldLongitude),
        save = translate(b.save),
        cancel = translate(b.cancel),
        confirmDeleteTitle = translate(b.confirmDeleteTitle),
        confirmDeleteMessage = translate(b.confirmDeleteMessage),
        confirmDelete = translate(b.confirmDelete),
        capacitePlaces = translate(b.capacitePlaces),
        searchHint = translate(b.searchHint)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInfrastructureScreen(
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var strings by remember { mutableStateOf(InfraStrings()) }
    LaunchedEffect(currentLanguage) {
        strings = languageViewModel.translateInfraStrings()
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(strings.tabVelo, strings.tabBus, strings.tabTram, strings.tabTer, strings.tabParking)

    Scaffold(
        topBar = {
            TopBar(languageViewModel = languageViewModel, onMenuClick = onBack, isBackButton = true)
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Text(
                text = strings.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = ObcampusPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                contentColor = ObcampusPrimary,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label) }
                    )
                }
            }

            when (selectedTab) {
                0 -> AdminStationVTab(strings = strings)
                1 -> AdminBusTab(strings = strings)
                2 -> AdminTramTab(strings = strings)
                3 -> AdminTerTab(strings = strings)
                4 -> AdminParkingTab(strings = strings)
            }
        }
    }
}