package com.ObservatoireCampus.mobile.viewmodel.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.weather.AirQualityAtDto
import com.ObservatoireCampus.mobile.model.weather.CurrentWeatherDto
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import com.ObservatoireCampus.mobile.model.weather.WeatherRequestDto
import com.ObservatoireCampus.mobile.repository.weather.AirQualityRepository
import com.ObservatoireCampus.mobile.repository.weather.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val CAMPUS_LAT = 44.808
private const val CAMPUS_LON = -0.595

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val airQualityRepository: AirQualityRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _currentWeather = MutableStateFlow<CurrentWeatherDto?>(null)
    val currentWeather: StateFlow<CurrentWeatherDto?> = _currentWeather.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _hourlyPoints = MutableStateFlow<List<HourlyWeatherPointDto>>(emptyList())
    val hourlyPoints: StateFlow<List<HourlyWeatherPointDto>> = _hourlyPoints.asStateFlow()

    private val _selectedHourIndex = MutableStateFlow(LocalTime.now().hour)
    val selectedHourIndex: StateFlow<Int> = _selectedHourIndex.asStateFlow()

    private val _airQualityDaily = MutableStateFlow<AirQualityAtDto?>(null)
    val airQualityDaily: StateFlow<AirQualityAtDto?> = _airQualityDaily.asStateFlow()

    private val _airQualityHour = MutableStateFlow<AirQualityAtDto?>(null)
    val airQualityHour: StateFlow<AirQualityAtDto?> = _airQualityHour.asStateFlow()

    private val _showHourlyAirQuality = MutableStateFlow(false)
    val showHourlyAirQuality: StateFlow<Boolean> = _showHourlyAirQuality.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // NOUVEAU : message affiche quand on retombe sur le campus par defaut
    private val _locationWarning = MutableStateFlow<String?>(null)
    val locationWarning: StateFlow<String?> = _locationWarning.asStateFlow()

    // NOUVEAU : coordonnees actuellement utilisees pour tous les appels (user ou campus)
    private var activeLat: Double = CAMPUS_LAT
    private var activeLon: Double = CAMPUS_LON

    // NOUVEAU : vrai si on utilise reellement la position de l'utilisateur
    private var usingUserLocation: Boolean = false

    /**
     * Point d'entree de l'ecran Meteo.
     * @param userLat / userLon : position de l'utilisateur (ex: LocationViewModel.userLocation).
     *        Si null (position non trouvee / permission refusee) -> fallback campus + warning.
     */
    fun loadInitial(userLat: Double? = null, userLon: Double? = null) {
        if (userLat != null && userLon != null) {
            activeLat = userLat
            activeLon = userLon
            usingUserLocation = true
            _locationWarning.value = null
        } else {
            activeLat = CAMPUS_LAT
            activeLon = CAMPUS_LON
            usingUserLocation = false
            _locationWarning.value = "Position non trouvée. Météo du campus affichée par défaut."
        }

        loadCurrentWeather()
        selectDate(LocalDate.now())
    }

    private fun loadCurrentWeather() {
        viewModelScope.launch {
            try {
                _currentWeather.value = if (usingUserLocation) {
                    // Position user -> on demande la meteo precise a l'heure actuelle
                    val now = LocalDate.now()
                    val nowTime = LocalTime.now()
                    val result = weatherRepository.getWeatherAt(
                        WeatherRequestDto(
                            latitude = activeLat,
                            longitude = activeLon,
                            date = now.format(dateFormatter),
                            time = String.format("%02d:00:00", nowTime.hour)
                        )
                    )
                    CurrentWeatherDto(
                        temperature = result.temperature,
                        windspeed = null,
                        winddirection = null,
                        weathercode = result.weathercode,
                        description = result.description,
                        icon = result.icon,
                        time = result.time,
                        isDay = null
                    )
                } else {
                    // Campus par defaut -> endpoint cache rapide
                    weatherRepository.getCurrent()
                }
            } catch (e: Exception) {
                _error.value = "Meteo actuelle indisponible"
            }
        }
    }

    /** Fleches du haut : jour precedent / suivant. */
    fun shiftDay(deltaDays: Long) {
        selectDate(_selectedDate.value.plusDays(deltaDays))
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _showHourlyAirQuality.value = false
        loadHourlyFor(date)
        loadAirQualityDailyFor(date)
    }

    /** Fleches de l'echelle horaire : heure precedente / suivante. */
    fun shiftHour(delta: Int) {
        val newIndex = (_selectedHourIndex.value + delta).coerceIn(0, 23)
        selectHour(newIndex)
    }

    /** Tap direct sur une heure de l'echelle -> bascule le panneau qualite de l'air sur cette heure. */
    fun selectHour(hourIndex: Int) {
        _selectedHourIndex.value = hourIndex
        _showHourlyAirQuality.value = true
        loadAirQualityHourFor(_selectedDate.value, hourIndex)
    }

    private fun loadHourlyFor(date: LocalDate) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _hourlyPoints.value = if (!usingUserLocation && date == LocalDate.now()) {
                    // Campus + aujourd'hui : deja dispo en un seul appel (cache backend, 24 points).
                    weatherRepository.getHourlyToday()
                } else {
                    // Position utilisateur, ou autre jour : pas d'endpoint "hourly" dedie pour
                    // des coordonnees/date arbitraires -> on reconstruit les 24 points via
                    // /weather/at (appels paralleles) avec les coordonnees actives.
                    (0..23).map { hour ->
                        async {
                            try {
                                val result = weatherRepository.getWeatherAt(
                                    WeatherRequestDto(
                                        latitude = activeLat,
                                        longitude = activeLon,
                                        date = date.format(dateFormatter),
                                        time = String.format("%02d:00:00", hour)
                                    )
                                )
                                HourlyWeatherPointDto(
                                    time = String.format("%02d:00", hour),
                                    temperature = result.temperature,
                                    weathercode = result.weathercode,
                                    description = result.description,
                                    icon = result.icon,
                                    precipitationProbability = result.precipitationProbability
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                _selectedHourIndex.value = if (date == LocalDate.now()) LocalTime.now().hour else 12
            } catch (e: Exception) {
                _error.value = "Donnees meteo indisponibles pour cette date"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadAirQualityDailyFor(date: LocalDate) {
        viewModelScope.launch {
            _airQualityDaily.value = try {
                airQualityRepository.getAirQualityAt(
                    WeatherRequestDto(
                        latitude = activeLat,
                        longitude = activeLon,
                        date = date.format(dateFormatter)
                    )
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun loadAirQualityHourFor(date: LocalDate, hourIndex: Int) {
        viewModelScope.launch {
            _airQualityHour.value = try {
                airQualityRepository.getAirQualityAt(
                    WeatherRequestDto(
                        latitude = activeLat,
                        longitude = activeLon,
                        date = date.format(dateFormatter),
                        time = String.format("%02d:00:00", hourIndex)
                    )
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}