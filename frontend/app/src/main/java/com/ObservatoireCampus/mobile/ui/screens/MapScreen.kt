package com.ObservatoireCampus.mobile.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import com.ObservatoireCampus.mobile.network.RetrofitClient
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.repository.ParkingRepository
import com.ObservatoireCampus.mobile.repository.station.StationTBRepository
import com.ObservatoireCampus.mobile.repository.station.StationVRepository
import com.ObservatoireCampus.mobile.repository.station.StationTerRepository
import com.ObservatoireCampus.mobile.ui.components.CampusButton
import com.ObservatoireCampus.mobile.ui.components.CampusLegend
import com.ObservatoireCampus.mobile.ui.components.LocationButton
import com.ObservatoireCampus.mobile.ui.components.CampusMap
import com.ObservatoireCampus.mobile.ui.components.DrawerMenu
import com.ObservatoireCampus.mobile.ui.components.ErrorBanner
import com.ObservatoireCampus.mobile.ui.components.SearchBar
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.ZoomControls
import com.ObservatoireCampus.mobile.ui.components.drawItineraryRoute
import com.ObservatoireCampus.mobile.ui.components.drawHistoryRoute
import com.ObservatoireCampus.mobile.ui.components.clearItineraryRoute
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryResultsList
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryDetailsDialog
import com.ObservatoireCampus.mobile.ui.components.weather.CurrentWeatherBadge
import com.ObservatoireCampus.mobile.ui.components.station.StationTBBubble
import com.ObservatoireCampus.mobile.ui.components.station.StationVBubble
import com.ObservatoireCampus.mobile.ui.components.station.StationTerBubble
import com.ObservatoireCampus.mobile.ui.components.parking.ParkingBubble
import com.ObservatoireCampus.mobile.ui.components.location.LocationBubble
import com.ObservatoireCampus.mobile.ui.components.location.upsertUserLocationMarker
import com.ObservatoireCampus.mobile.ui.components.location.removeUserLocationMarker
import com.ObservatoireCampus.mobile.ui.theme.ObcampusBackground
import com.ObservatoireCampus.mobile.viewmodel.MapViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationTBViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationTBViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationVViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationVViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationTerViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationTerViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.freevehicle.FreeVehicleViewModel
import com.ObservatoireCampus.mobile.viewmodel.freevehicle.FreeVehicleViewModelFactory
import com.ObservatoireCampus.mobile.repository.freevehicle.FreeVehicleRepository
import com.ObservatoireCampus.mobile.ui.components.freevehicle.FreeVehicleBubble
import androidx.compose.ui.platform.LocalContext
import com.ObservatoireCampus.mobile.viewmodel.location.LocationViewModel
import com.ObservatoireCampus.mobile.viewmodel.location.LocationViewModelFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.search.SearchViewModel
import com.ObservatoireCampus.mobile.ui.components.search.SearchResultInfoWindow
import com.ObservatoireCampus.mobile.ui.components.search.createSearchResultMarkerIcon
import com.ObservatoireCampus.mobile.ui.components.search.createOriginMarkerIcon
import com.ObservatoireCampus.mobile.ui.components.Search.ItinerarySearchPanel
import com.ObservatoireCampus.mobile.viewmodel.itinerary.ItineraryViewModel
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import org.osmdroid.util.BoundingBox
// Layout & Alignement
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

// Material 3 UI & Icônes
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List

/** Indique quel champ du panneau itineraire attend la position GPS de l'utilisateur. */
private enum class ItineraryLocationTarget { ORIGIN, DESTINATION }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    languageViewModel: LanguageViewModel,
    onWeatherClick: (Double?, Double?) -> Unit = { _, _ -> },
    onInternshipClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    historyItemToShow: SearchHistoryDto? = null,
    onHistoryItemShown: () -> Unit = {},
    onUserManagementClick: () -> Unit = {},
    onInfrastructureClick: () -> Unit = {},
    onLegendsClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val campusList by viewModel.campusList.collectAsState()
    val batimentList by viewModel.batimentList.collectAsState()
    val legendList by viewModel.legendList.collectAsState()
    val campusError by viewModel.error.collectAsState()
    var showLegendBottomSheet by remember { mutableStateOf(false) }

    val parkingViewModel: ParkingViewModel = viewModel(
        factory = ParkingViewModelFactory(ParkingRepository())
    )
    val parkingLayers by parkingViewModel.parkingLayers.collectAsState()
    val visibleParking by parkingViewModel.visiblePositions.collectAsState()
    val parkingError by parkingViewModel.error.collectAsState()
    val selectedParkingId by parkingViewModel.selectedParkingId.collectAsState()
    val selectedParkingStatus by parkingViewModel.selectedParkingStatus.collectAsState()
    val bubbleLoadingParking by parkingViewModel.bubbleLoading.collectAsState()
    var parkingExpanded by remember { mutableStateOf(false) }

    // Bus / Tram
    val stationTBViewModel: StationTBViewModel = viewModel(
        factory = StationTBViewModelFactory(StationTBRepository())
    )
    val stationTBLayers by stationTBViewModel.layers.collectAsState()
    val visibleStationsTB by stationTBViewModel.visiblePositions.collectAsState()
    val selectedStationTB by stationTBViewModel.selectedStation.collectAsState()
    val passagesTB by stationTBViewModel.passages.collectAsState()
    val bubbleLoadingTB by stationTBViewModel.bubbleLoading.collectAsState()
    val stationTBError by stationTBViewModel.error.collectAsState()
    val stationTBPassagesError by stationTBViewModel.passagesError.collectAsState()
    var stationTBExpanded by remember { mutableStateOf(false) }

    // Velo
    val stationVViewModel: StationVViewModel = viewModel(
        factory = StationVViewModelFactory(StationVRepository())
    )
    val stationVLayers by stationVViewModel.layers.collectAsState()
    val visibleStationsV by stationVViewModel.visiblePositions.collectAsState()
    val selectedStationVDetail by stationVViewModel.selectedDetail.collectAsState()
    val bubbleLoadingV by stationVViewModel.bubbleLoading.collectAsState()
    val stationVError by stationVViewModel.error.collectAsState()
    var stationVExpanded by remember { mutableStateOf(false) }

    // TER
    val stationTerViewModel: StationTerViewModel = viewModel(
        factory = StationTerViewModelFactory(StationTerRepository())
    )
    val stationTerLayers by stationTerViewModel.layers.collectAsState()
    val visibleStationsTer by stationTerViewModel.visiblePositions.collectAsState()
    val selectedStationTer by stationTerViewModel.selectedStation.collectAsState()
    val passagesTer by stationTerViewModel.passages.collectAsState()
    val bubbleLoadingTer by stationTerViewModel.bubbleLoading.collectAsState()
    val stationTerError by stationTerViewModel.error.collectAsState()
    val stationTerPassagesError by stationTerViewModel.passagesError.collectAsState()
    var stationTerExpanded by remember { mutableStateOf(false) }

    // Free vehicle
    val freeVehicleViewModel: FreeVehicleViewModel = viewModel(
        factory = FreeVehicleViewModelFactory(FreeVehicleRepository())
    )
    val freeVehicleLayers by freeVehicleViewModel.layers.collectAsState()
    val visibleFreeVehicles by freeVehicleViewModel.visiblePositions.collectAsState()
    val freeVehicleError by freeVehicleViewModel.error.collectAsState()
    val selectedFreeVehicleId by freeVehicleViewModel.selectedVehicleId.collectAsState()
    val selectedFreeVehicle by freeVehicleViewModel.selectedVehicle.collectAsState()
    val bubbleLoadingFV by freeVehicleViewModel.bubbleLoading.collectAsState()
    val selectedStationV by stationVViewModel.selectedStation.collectAsState()
    val stationVDetailError by stationVViewModel.detailError.collectAsState()
    var freeVehicleExpanded by remember { mutableStateOf(false) }

    // Localisation utilisateur
    val context = LocalContext.current
    val isAdmin = remember { RetrofitClient.getTokenManager().isAdmin() }
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(LocationServices.getFusedLocationProviderClient(context))
    )
    val userLocation by locationViewModel.userLocation.collectAsState()
    val locationBubbleVisible by locationViewModel.bubbleVisible.collectAsState()
    val locationAccuracy by locationViewModel.accuracyMeters.collectAsState()
    val locationState by locationViewModel.locationState.collectAsState()

    // Permission de localisation pour les boutons "cible" du panneau itineraire
    var pendingLocationTarget by remember { mutableStateOf<ItineraryLocationTarget?>(null) }

    // ---------- ITINERAIRE ----------
    val itineraryViewModel: ItineraryViewModel = viewModel()
    val originQuery by itineraryViewModel.originQuery.collectAsState()
    val originSuggestions by itineraryViewModel.originSuggestions.collectAsState()
    val originPoint by itineraryViewModel.originPoint.collectAsState()
    val destinationQuery by itineraryViewModel.destinationQuery.collectAsState()
    val destinationSuggestions by itineraryViewModel.destinationSuggestions.collectAsState()
    val destinationPoint by itineraryViewModel.destinationPoint.collectAsState()
    val sortedItineraryOptions by itineraryViewModel.sortedItineraryOptions.collectAsState()
    val itineraryFilters by itineraryViewModel.filters.collectAsState()
    val selectedItinerary by itineraryViewModel.selectedItinerary.collectAsState()
    val isSearchingItinerary by itineraryViewModel.isSearching.collectAsState()
    val itinerarySearchError by itineraryViewModel.searchError.collectAsState()
    val detailsItinerary by itineraryViewModel.detailsItinerary.collectAsState()
    val savedHistorySignatures by itineraryViewModel.savedHistorySignatures.collectAsState()
    val savingHistorySignatures by itineraryViewModel.savingHistorySignatures.collectAsState()
    val saveHistoryMessage by itineraryViewModel.saveHistoryMessage.collectAsState()

    var showItineraryPanel by remember { mutableStateOf(false) }
    val itinerarySheetState = rememberModalBottomSheetState()
    var itineraryOriginMarker by remember { mutableStateOf<Marker?>(null) }
    var itineraryDestinationMarker by remember { mutableStateOf<Marker?>(null) }
    var itineraryRoutePolylines by remember { mutableStateOf<List<Polyline>>(emptyList()) }
    var historyRoutePolylines by remember { mutableStateOf<List<Polyline>>(emptyList()) }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    val itineraryLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationViewModel.fetchLocation()
        } else {
            locationViewModel.onPermissionDenied()
            pendingLocationTarget = null
        }
    }

    fun requestLocationForItinerary(target: ItineraryLocationTarget) {
        pendingLocationTarget = target
        if (hasLocationPermission()) {
            locationViewModel.fetchLocation()
        } else {
            itineraryLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Langue
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val isTranslating by languageViewModel.isTranslating.collectAsState()
    val languageError by languageViewModel.error.collectAsState()

    // Search
    val searchViewModel: SearchViewModel = viewModel()
    val searchQuery by searchViewModel.query.collectAsState()
    val searchSuggestions by searchViewModel.suggestions.collectAsState()

    var searchMarker by remember { mutableStateOf<Marker?>(null) }
    var displayedCampusList by remember { mutableStateOf(campusList) }
    var translatedUserLocationPinTitle by remember { mutableStateOf("Ma position") }

    LaunchedEffect(currentLanguage, campusList) {
        translatedUserLocationPinTitle = languageViewModel.translate("Ma position")

        displayedCampusList = if (currentLanguage == AppLanguage.FR) {
            campusList
        } else {
            campusList.map { campus ->
                val nomTraduit = languageViewModel.translate(campus.name)
                campus.copy(name = nomTraduit)
            }
        }
    }

    val combinedError =
        listOfNotNull(
            campusError, parkingError, stationTBError, stationVError,
            stationTerError, freeVehicleError, languageError
        )
            .distinct()
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" | ")

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var showCampus by remember { mutableStateOf(false) }

    var userLocationMarker by remember { mutableStateOf<Marker?>(null) }

    LaunchedEffect(Unit) {
        if (campusList.isEmpty()) viewModel.loadCampus()
        parkingViewModel.loadParking()
        stationTBViewModel.loadStations()
        stationVViewModel.loadStations()
        stationTerViewModel.loadStations()
        freeVehicleViewModel.loadStations()
    }

    LaunchedEffect(userLocation, pendingLocationTarget, translatedUserLocationPinTitle) {
        val target = pendingLocationTarget ?: return@LaunchedEffect
        val point = userLocation ?: return@LaunchedEffect

        when (target) {
            ItineraryLocationTarget.ORIGIN -> itineraryViewModel.setOriginToMyLocation(
                point.latitude, point.longitude, translatedUserLocationPinTitle
            )
            ItineraryLocationTarget.DESTINATION -> itineraryViewModel.setDestinationToMyLocation(
                point.latitude, point.longitude, translatedUserLocationPinTitle
            )
        }
        pendingLocationTarget = null
    }

    LaunchedEffect(userLocation, mapView, translatedUserLocationPinTitle) {
        val currentMapView = mapView ?: return@LaunchedEffect
        val point = userLocation

        if (point != null) {
            userLocationMarker = upsertUserLocationMarker(
                mapView = currentMapView,
                existing = userLocationMarker,
                point = point,
                titleText = translatedUserLocationPinTitle,
                onClick = { locationViewModel.onMarkerClicked() }
            )
            currentMapView.controller.setZoom(18.0)
            currentMapView.controller.animateTo(point)
        } else if (userLocationMarker != null) {
            removeUserLocationMarker(currentMapView, userLocationMarker)
            userLocationMarker = null
        }
    }

    // Dessine / efface le trace des qu'une option d'itineraire est selectionnee.
    LaunchedEffect(selectedItinerary, mapView) {
        val mp = mapView ?: return@LaunchedEffect
        clearItineraryRoute(mp, itineraryRoutePolylines)
        itineraryRoutePolylines = selectedItinerary?.let { drawItineraryRoute(mp, it) } ?: emptyList()
    }
    LaunchedEffect(historyItemToShow, mapView) {
        val mp = mapView ?: return@LaunchedEffect
        val item = historyItemToShow ?: return@LaunchedEffect

        fun drawAndZoom() {
            clearItineraryRoute(mp, historyRoutePolylines)
            historyRoutePolylines = drawHistoryRoute(mp, item)

            val points = item.legs
                .filter { it.fromLat != 0.0 && it.toLat != 0.0 }
                .flatMap { listOf(GeoPoint(it.fromLat, it.fromLon), GeoPoint(it.toLat, it.toLon)) }
            val distinctPoints = points.distinct()

            // Garde-fou : une bounding box de largeur/hauteur quasi nulle (un seul
            // point distinct, trajet tres court, ou vieil historique sans
            // coordonnees) fait boucler/freezer zoomToBoundingBox() dans osmdroid.
            // On centre manuellement dans ce cas plutot que de zoomer sur une box.
            when {
                distinctPoints.size >= 2 -> {
                    val box = BoundingBox.fromGeoPoints(points)
                    val latSpan = box.latNorth - box.latSouth
                    val lonSpan = box.lonEast - box.lonWest
                    if (latSpan > 0.0001 || lonSpan > 0.0001) {
                        mp.zoomToBoundingBox(box, true, 100)
                    } else {
                        mp.controller.setZoom(17.0)
                        mp.controller.animateTo(distinctPoints.first())
                    }
                }
                distinctPoints.size == 1 -> {
                    mp.controller.setZoom(17.0)
                    mp.controller.animateTo(distinctPoints.first())
                }
                // sinon : vieil historique sans coordonnees valides, on ne touche pas la camera
            }
            onHistoryItemShown()
        }

        // La MapView vient parfois d'etre recreee (retour depuis l'Historique) et n'a
        // pas encore ete mesuree par Android : zoomToBoundingBox() ne fait rien tant
        // que width/height valent 0. On attend le premier passage de layout reel.
        if (mp.width > 0 && mp.height > 0) {
            drawAndZoom()
        } else {
            mp.viewTreeObserver.addOnGlobalLayoutListener(object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mp.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    drawAndZoom()
                }
            })
        }
    }

    // Toast simple apres tentative d'enregistrement dans l'historique.
    LaunchedEffect(saveHistoryMessage) {
        saveHistoryMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
            itineraryViewModel.clearSaveHistoryMessage()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            DrawerMenu(
                languageViewModel = languageViewModel,
                isAdmin = isAdmin,
                parkingLayers = parkingLayers,
                parkingMasterActive = parkingViewModel.masterActive,
                parkingExpanded = parkingExpanded,
                onParkingExpandToggle = { parkingExpanded = !parkingExpanded },
                onParkingMasterToggle = { parkingViewModel.toggleMaster() },
                onParkingItemToggle = { key -> parkingViewModel.toggleType(key) },
                stationTBLayers = stationTBLayers,
                stationTBMasterActive = stationTBViewModel.masterActive,
                stationTBExpanded = stationTBExpanded,
                onStationTBExpandToggle = { stationTBExpanded = !stationTBExpanded },
                onStationTBMasterToggle = { stationTBViewModel.toggleMaster() },
                onStationTBItemToggle = { key -> stationTBViewModel.toggleType(key) },
                stationVLayers = stationVLayers,
                stationVMasterActive = stationVViewModel.masterActive,
                stationVExpanded = stationVExpanded,
                onStationVExpandToggle = { stationVExpanded = !stationVExpanded },
                onStationVMasterToggle = { stationVViewModel.toggleMaster() },
                onStationVItemToggle = { key -> stationVViewModel.toggleType(key) },
                stationTerLayers = stationTerLayers,
                stationTerMasterActive = stationTerViewModel.masterActive,
                stationTerExpanded = stationTerExpanded,
                onStationTerExpandToggle = { stationTerExpanded = !stationTerExpanded },
                onStationTerMasterToggle = { stationTerViewModel.toggleMaster() },
                onStationTerItemToggle = { key -> stationTerViewModel.toggleType(key) },
                freeVehicleLayers = freeVehicleLayers,
                freeVehicleMasterActive = freeVehicleViewModel.masterActive,
                freeVehicleExpanded = freeVehicleExpanded,
                onFreeVehicleExpandToggle = { freeVehicleExpanded = !freeVehicleExpanded },
                onFreeVehicleMasterToggle = { freeVehicleViewModel.toggleMaster() },
                onFreeVehicleItemToggle = { key -> freeVehicleViewModel.toggleType(key) },
                currentLanguage = currentLanguage,
                isTranslating = isTranslating,
                onLanguageSelected = { selectedLang ->
                    languageViewModel.setLanguage(selectedLang)
                },
                onWeatherClick = {
                    scope.launch { drawerState.close() }
                    onWeatherClick(userLocation?.latitude, userLocation?.longitude)
                },
                onInternshipClick = {
                    scope.launch { drawerState.close() }
                    onInternshipClick()
                },
                onAccountClick = {
                    scope.launch { drawerState.close() }
                    onAccountClick()
                },
                onItineraryClick = {
                    scope.launch { drawerState.close() }
                    showItineraryPanel = true
                },
                onHistoryClick = {
                    scope.launch { drawerState.close() }
                    onHistoryClick()
                },
                onUserManagementClick = {
                    scope.launch { drawerState.close() }
                    onUserManagementClick()
                },
                onInfrastructureClick = {
                    scope.launch { drawerState.close() }
                    onInfrastructureClick()
                },
                onLegendsClick = {
                    scope.launch { drawerState.close() }
                    onLegendsClick()
                },
                onBackToMap = { scope.launch { drawerState.close() } },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObcampusBackground)
        ) {
            // CORRECTIF : le key(displayedCampusList) qui entourait cet appel a ete
            // retire. displayedCampusList change de reference a chaque traduction
            // (LaunchedEffect(currentLanguage, campusList) cree une nouvelle liste),
            // et key() detruisait alors ENTIEREMENT CampusMap -> demontait la MapView
            // osmdroid en cours et en remontait une nouvelle. Si ca se produisait juste
            // apres un retour depuis l'Historique, le listener de layout pose sur
            // l'ancienne MapView (dans le LaunchedEffect(historyItemToShow, mapView)
            // de ce fichier) ne se declenchait jamais car une vue detachee n'emet plus
            // d'evenements de layout -> "Voir sur la carte" ne dessinait jamais rien.
            // CampusMap reagit deja en interne aux changements de campusList via son
            // propre LaunchedEffect, pas besoin de le detruire/recreer pour ca.
            CampusMap(
                campusList = displayedCampusList,
                showPolygons = showCampus,
                languageViewModel = languageViewModel,
                batimentList = batimentList,
                parkingList = visibleParking,
                onParkingClick = { parkingViewModel.onParkingClicked(it.id) },
                stationTBList = visibleStationsTB,
                onStationTBClick = { stationTBViewModel.onStationClicked(it) },
                stationVList = visibleStationsV,
                onStationVClick = { stationVViewModel.onStationClicked(it) },
                stationTerList = visibleStationsTer,
                onStationTerClick = { stationTerViewModel.onStationClicked(it) },
                freeVehicleList = visibleFreeVehicles,
                onFreeVehicleClick = { freeVehicleViewModel.onVehicleClicked(it.bikeId) },
                onMapReady = { mapView = it },
                modifier = Modifier.fillMaxSize()
            )

            TopBar(
                languageViewModel = languageViewModel,
                onMenuClick = { scope.launch { drawerState.open() } }
            )

            SearchBar(
                languageViewModel = languageViewModel,
                query = searchQuery,
                suggestions = searchSuggestions,
                onQueryChange = { searchViewModel.onQueryChanged(it) },
                onSuggestionSelected = { selectedPlace ->
                    val targetPoint = GeoPoint(selectedPlace.latitude, selectedPlace.longitude)

                    mapView?.let { mp ->
                        mp.controller.animateTo(targetPoint)
                        mp.controller.setZoom(17.0)

                        searchMarker?.let { mp.overlays.remove(it) }

                        val newMarker = Marker(mp).apply {
                            position = targetPoint
                            title = selectedPlace.name
                            snippet = selectedPlace.subtitle
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = createSearchResultMarkerIcon(mp.context)
                            infoWindow = SearchResultInfoWindow(mp)
                            setOnMarkerClickListener { clickedMarker, _ ->
                                if (clickedMarker.isInfoWindowShown) {
                                    clickedMarker.closeInfoWindow()
                                } else {
                                    org.osmdroid.views.overlay.infowindow.InfoWindow.closeAllInfoWindowsOn(mp)
                                    clickedMarker.showInfoWindow()
                                }
                                true
                            }
                        }
                        mp.overlays.add(newMarker)
                        searchMarker = newMarker
                        newMarker.showInfoWindow()
                        mp.invalidate()
                    }

                    searchViewModel.onQueryChanged("")
                },
                onOpenItinerary = { showItineraryPanel = true },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
            )

            if (showItineraryPanel) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showItineraryPanel = false
                        pendingLocationTarget = null
                    },
                    sheetState = itinerarySheetState
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ItinerarySearchPanel(
                            originQuery = originQuery,
                            originSuggestions = originSuggestions,
                            onOriginQueryChange = { itineraryViewModel.onOriginQueryChanged(it) },
                            onOriginSuggestionSelected = { itineraryViewModel.selectOrigin(it) },
                            onUseMyLocationAsOrigin = {
                                requestLocationForItinerary(ItineraryLocationTarget.ORIGIN)
                            },
                            destinationQuery = destinationQuery,
                            destinationSuggestions = destinationSuggestions,
                            onDestinationQueryChange = { itineraryViewModel.onDestinationQueryChanged(it) },
                            onDestinationSuggestionSelected = { itineraryViewModel.selectDestination(it) },
                            onUseMyLocationAsDestination = {
                                requestLocationForItinerary(ItineraryLocationTarget.DESTINATION)
                            },
                            filters = itineraryFilters,
                            onModeToggle = itineraryViewModel::toggleMode,
                            onTimeChange = itineraryViewModel::updateTimeFilter,
                            onSortChange = itineraryViewModel::updateSortOption,
                            canSearch = originPoint != null && destinationPoint != null,
                            onSearchClick = {
                                val origin = originPoint
                                val destination = destinationPoint
                                if (origin != null && destination != null) {
                                    mapView?.let { mp ->
                                        val originGeoPoint = GeoPoint(origin.latitude, origin.longitude)
                                        val destinationGeoPoint = GeoPoint(destination.latitude, destination.longitude)

                                        itineraryOriginMarker?.let { mp.overlays.remove(it) }
                                        itineraryDestinationMarker?.let { mp.overlays.remove(it) }

                                        val newOriginMarker = Marker(mp).apply {
                                            position = originGeoPoint
                                            title = if (origin.name == com.ObservatoireCampus.mobile.model.search.CURRENT_LOCATION_MARKER)
                                                translatedUserLocationPinTitle else origin.name
                                            snippet = origin.subtitle
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            icon = createOriginMarkerIcon(mp.context)
                                            infoWindow = SearchResultInfoWindow(mp)
                                        }
                                        val newDestinationMarker = Marker(mp).apply {
                                            position = destinationGeoPoint
                                            title = if (destination.name == com.ObservatoireCampus.mobile.model.search.CURRENT_LOCATION_MARKER)
                                                translatedUserLocationPinTitle else destination.name
                                            snippet = destination.subtitle
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            icon = createSearchResultMarkerIcon(mp.context)
                                            infoWindow = SearchResultInfoWindow(mp)
                                        }

                                        mp.overlays.add(newOriginMarker)
                                        mp.overlays.add(newDestinationMarker)
                                        itineraryOriginMarker = newOriginMarker
                                        itineraryDestinationMarker = newDestinationMarker

                                        val boundingBox = BoundingBox.fromGeoPoints(
                                            listOf(originGeoPoint, destinationGeoPoint)
                                        )
                                        mp.zoomToBoundingBox(boundingBox, true, 100)
                                        mp.invalidate()
                                    }
                                }

                                itineraryViewModel.submitItinerary()
                            },
                            onResetClick = {
                                mapView?.let { mp ->
                                    itineraryOriginMarker?.let { mp.overlays.remove(it) }
                                    itineraryDestinationMarker?.let { mp.overlays.remove(it) }
                                    itineraryOriginMarker = null
                                    itineraryDestinationMarker = null
                                    mp.invalidate()
                                }
                                itineraryViewModel.resetAllFields()
                            },
                            languageViewModel = languageViewModel,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ItineraryResultsList(
                            options = sortedItineraryOptions,
                            selectedItinerary = selectedItinerary,
                            onOptionClick = { itineraryViewModel.showItineraryDetails(it) },
                            savedOptionSignatures = savedHistorySignatures,
                            savingOptionSignatures = savingHistorySignatures,
                            onSaveClick = { itineraryViewModel.saveToHistory(it) },
                            isLoading = isSearchingItinerary,
                            errorMessage = itinerarySearchError,
                            languageViewModel = languageViewModel
                        )
                    }
                }
            }

            CampusButton(
                languageViewModel = languageViewModel,
                onClick = {
                    showCampus = !showCampus
                    // S'il est activé, on zoom et on centre automatiquement
                    if (showCampus) {
                        displayedCampusList.firstOrNull()?.let { campus ->
                            mapView?.controller?.apply {
                                setZoom(16.0) // Ajuste le zoom selon la précision souhaitée
                                animateTo(GeoPoint(campus.centerLat, campus.centerLng))
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 10.dp)
            )



            LocationButton(
                viewModel = locationViewModel,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 62.dp)
            )

            // Conteneur aligné en bas à droite pour le Zoom et le Bouton Légende
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Boutons Zoom (+) et (-)
                ZoomControls(
                    onZoomIn = { mapView?.controller?.zoomIn() },
                    onZoomOut = { mapView?.controller?.zoomOut() }
                )

                // Affiche le bouton Légende juste en bas du Zoom UNIQUEMENT si les campus sont affichés
                if (showCampus) {
                    Surface(
                        onClick = { showLegendBottomSheet = !showLegendBottomSheet },
                        modifier = Modifier.size(40.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.List,
                                contentDescription = "Légende",
                                tint = com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
                            )
                        }
                    }
                }
            }

            CurrentWeatherBadge(
                onClick = { onWeatherClick(userLocation?.latitude, userLocation?.longitude) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 24.dp, start = 16.dp)
            )

            ErrorBanner(
                error = combinedError,
                languageViewModel = languageViewModel,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 116.dp, start = 16.dp, end = 16.dp)
            )

            if (selectedParkingId != null) {
                key(currentLanguage) {
                    ParkingBubble(
                        status = selectedParkingStatus,
                        loading = bubbleLoadingParking,
                        onClose = { parkingViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedStationTB != null) {
                key(currentLanguage) {
                    StationTBBubble(
                        station = selectedStationTB!!,
                        passages = passagesTB,
                        loading = bubbleLoadingTB,
                        onClose = { stationTBViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        errorMessage = stationTBPassagesError,
                        onRetry = { stationTBViewModel.retryPassages() },
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedStationVDetail != null) {
                val positionCorrespondante = visibleStationsV.find { it.stationId == selectedStationVDetail!!.stationId }
                    ?: StationVPositionDto(0L, selectedStationVDetail!!.stationId, selectedStationVDetail!!.nom ?: "Station", selectedStationVDetail!!.latitude, selectedStationVDetail!!.longitude)

                key(currentLanguage) {
                    StationVBubble(
                        position = selectedStationV!!,
                        detail = selectedStationVDetail,
                        loading = bubbleLoadingV,
                        errorMessage = stationVDetailError,
                        onRetry = { stationVViewModel.retryDetail() },
                        onClose = { stationVViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedStationTer != null) {
                key(currentLanguage) {
                    StationTerBubble(
                        station = selectedStationTer!!,
                        passages = passagesTer,
                        loading = bubbleLoadingTer,
                        onClose = { stationTerViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        errorMessage = stationTerPassagesError,
                        onRetry = { stationTerViewModel.retryPassages() },
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedFreeVehicleId != null) {
                key(currentLanguage) {
                    FreeVehicleBubble(
                        detail = selectedFreeVehicle,
                        loading = bubbleLoadingFV,
                        onClose = { freeVehicleViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (locationBubbleVisible) {
                key(currentLanguage) {
                    LocationBubble(
                        point = userLocation,
                        loading = locationState is com.ObservatoireCampus.mobile.viewmodel.location.LocationUiState.Loading,
                        accuracyMeters = locationAccuracy,
                        onClose = { locationViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
            if (showCampus && showLegendBottomSheet && batimentList.isNotEmpty()) {
                ModalBottomSheet(
                    onDismissRequest = { showLegendBottomSheet = false }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Légende des Bâtiments",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        CampusLegend(
                            batimentList = batimentList,
                            legendList = legendList,
                            languageViewModel = languageViewModel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            detailsItinerary?.let { option ->
                ItineraryDetailsDialog(
                    option = option,
                    onDismiss = { itineraryViewModel.dismissItineraryDetails() },
                    onViewOnMap = {
                        itineraryViewModel.selectItinerary(option)
                        itineraryViewModel.dismissItineraryDetails()
                        showItineraryPanel = false
                    },
                    languageViewModel = languageViewModel
                )
            }
        }
    }
}