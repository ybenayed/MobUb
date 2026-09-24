package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.repository.weather.AirQualityRepository
import com.ObservatoireCampus.mobile.repository.weather.WeatherRepository
import com.ObservatoireCampus.mobile.ui.components.ErrorBanner
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.weather.*
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.weather.WeatherViewModel
import com.ObservatoireCampus.mobile.viewmodel.weather.WeatherViewModelFactory

@Composable
fun WeatherScreen(
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit,
    userLat: Double? = null,
    userLon: Double? = null,
    viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(WeatherRepository(), AirQualityRepository())
    )
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val hourlyPoints by viewModel.hourlyPoints.collectAsState()
    val selectedHourIndex by viewModel.selectedHourIndex.collectAsState()
    val airQualityDaily by viewModel.airQualityDaily.collectAsState()
    val airQualityHour by viewModel.airQualityHour.collectAsState()
    val showHourlyAirQuality by viewModel.showHourlyAirQuality.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val locationWarning by viewModel.locationWarning.collectAsState()

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedLocationWarning by remember { mutableStateOf<String?>(null) }
    var translatedError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadInitial(userLat = userLat, userLon = userLon)
    }

    LaunchedEffect(currentLanguage, locationWarning, error) {
        translatedLocationWarning = locationWarning?.let { languageViewModel.translate(it) }
        translatedError = error?.let { languageViewModel.translate(it) }
    }

    val currentIconUrl = hourlyPoints.getOrNull(selectedHourIndex)?.icon

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            languageViewModel = languageViewModel,
            onMenuClick = onBack,
            isBackButton = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                WeatherBackgroundArt(currentIconUrl = currentIconUrl)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header de date et d'heure locales réelles adaptées à la langue
                    CurrentDateTimeHeader(
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Navigateur de jours adapté à la langue
                    WeekDayNavigator(
                        selectedDate = selectedDate,
                        onPrevious = { viewModel.shiftDay(-1) },
                        onNext = { viewModel.shiftDay(1) },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (hourlyPoints.isNotEmpty()) {
                        // Courbe de température (avec pop-up d'informations traduit)
                        TemperatureCurve(
                            points = hourlyPoints,
                            selectedIndex = selectedHourIndex,
                            languageViewModel = languageViewModel,
                            currentLanguage = currentLanguage,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Affichage des bannières avec les textes traduits
            if (translatedLocationWarning != null) {
                ErrorBanner(
                    error = translatedLocationWarning,
                    languageViewModel = languageViewModel,
                    modifier = Modifier.padding(12.dp)
                )
            }

            if (translatedError != null) {
                ErrorBanner(
                    error = translatedError,
                    languageViewModel = languageViewModel,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObcampusPrimary)
                    .padding(vertical = 12.dp)
            ) {
                // Échelle horaire (avec accessibilité et descriptions météo traduites)
                HourlyWeatherScale(
                    points = hourlyPoints,
                    selectedIndex = selectedHourIndex,
                    onHourSelected = { viewModel.selectHour(it) },
                    languageViewModel = languageViewModel,
                    currentLanguage = currentLanguage
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Panneau de qualité de l'air entièrement traduit (descriptions & polluants)
            AirQualityPanel(
                dailyData = airQualityDaily,
                hourData = airQualityHour,
                showHourly = showHourlyAirQuality,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}