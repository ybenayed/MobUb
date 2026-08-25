package com.ObservatoireCampus.mobile.viewmodel.itinerary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.repository.ItineraryRepository
import com.ObservatoireCampus.mobile.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ItineraryViewModel"

/**
 * Gère les 2 champs (origine / destination) du panneau "Itinéraire".
 * Comme SearchViewModel, passe toujours par le backend via SearchRepository
 * (jamais d'appel Nominatim direct depuis l'app).
 */
class ItineraryViewModel : ViewModel() {

    private val repository = SearchRepository()
    private val itineraryRepository = ItineraryRepository()

    // ---------- ORIGINE ----------
    private val _originQuery = MutableStateFlow("")
    val originQuery: StateFlow<String> = _originQuery.asStateFlow()

    private val _originSuggestions = MutableStateFlow<List<SearchResultDto>>(emptyList())
    val originSuggestions: StateFlow<List<SearchResultDto>> = _originSuggestions.asStateFlow()

    private val _originLoading = MutableStateFlow(false)
    val originLoading: StateFlow<Boolean> = _originLoading.asStateFlow()

    private val _originPoint = MutableStateFlow<SearchResultDto?>(null)
    val originPoint: StateFlow<SearchResultDto?> = _originPoint.asStateFlow()

    // ---------- DESTINATION ----------
    private val _destinationQuery = MutableStateFlow("")
    val destinationQuery: StateFlow<String> = _destinationQuery.asStateFlow()

    private val _destinationSuggestions = MutableStateFlow<List<SearchResultDto>>(emptyList())
    val destinationSuggestions: StateFlow<List<SearchResultDto>> = _destinationSuggestions.asStateFlow()

    private val _destinationLoading = MutableStateFlow(false)
    val destinationLoading: StateFlow<Boolean> = _destinationLoading.asStateFlow()

    private val _destinationPoint = MutableStateFlow<SearchResultDto?>(null)
    val destinationPoint: StateFlow<SearchResultDto?> = _destinationPoint.asStateFlow()

    private var originJob: Job? = null
    private var destinationJob: Job? = null

    // ----- Origine -----
    fun onOriginQueryChanged(newQuery: String) {
        _originQuery.value = newQuery
        _originPoint.value = null // toute frappe manuelle invalide la sélection précédente
        originJob?.cancel()

        if (newQuery.isBlank() || newQuery.length < 3) {
            _originSuggestions.value = emptyList()
            _originLoading.value = false
            return
        }

        originJob = viewModelScope.launch {
            _originLoading.value = true
            delay(500)
            _originSuggestions.value = try {
                repository.searchPlaces(newQuery)
            } catch (e: Exception) {
                emptyList()
            }
            _originLoading.value = false
        }
    }

    fun selectOrigin(result: SearchResultDto) {
        originJob?.cancel()
        _originPoint.value = result
        _originQuery.value = result.name
        _originSuggestions.value = emptyList()
    }

    /** Appelé par le bouton "cible" 🎯 du champ origine. */
    fun setOriginToMyLocation(latitude: Double, longitude: Double, label: String = "Ma position") {
        originJob?.cancel()
        _originPoint.value = SearchResultDto(name = label, latitude = latitude, longitude = longitude, subtitle = "")
        _originQuery.value = label
        _originSuggestions.value = emptyList()
    }

    // ----- Destination -----
    fun onDestinationQueryChanged(newQuery: String) {
        _destinationQuery.value = newQuery
        _destinationPoint.value = null
        destinationJob?.cancel()

        if (newQuery.isBlank() || newQuery.length < 3) {
            _destinationSuggestions.value = emptyList()
            _destinationLoading.value = false
            return
        }

        destinationJob = viewModelScope.launch {
            _destinationLoading.value = true
            delay(500)
            _destinationSuggestions.value = try {
                repository.searchPlaces(newQuery)
            } catch (e: Exception) {
                emptyList()
            }
            _destinationLoading.value = false
        }
    }

    fun selectDestination(result: SearchResultDto) {
        destinationJob?.cancel()
        _destinationPoint.value = result
        _destinationQuery.value = result.name
        _destinationSuggestions.value = emptyList()
    }

    /** Appelé par le bouton "cible" 🎯 du champ destination. */
    fun setDestinationToMyLocation(latitude: Double, longitude: Double, label: String = "Ma position") {
        destinationJob?.cancel()
        _destinationPoint.value = SearchResultDto(name = label, latitude = latitude, longitude = longitude, subtitle = "")
        _destinationQuery.value = label
        _destinationSuggestions.value = emptyList()
    }

    /**
     * Envoie l'origine + la destination au backend (POST /api/itinerary).
     * Pour l'instant le backend se contente de logguer les 2 points dans sa console.
     * Ne fait rien si l'un des 2 points n'est pas encore choisi.
     */
    fun submitItinerary() {
        val origin = _originPoint.value
        val destination = _destinationPoint.value

        if (origin == null || destination == null) {
            Log.w(TAG, "submitItinerary() annulé : origin=$origin destination=$destination")
            return
        }

        Log.d(TAG, "Envoi vers le backend -> origin=$origin destination=$destination")

        viewModelScope.launch {
            try {
                itineraryRepository.sendItinerary(origin, destination)
                Log.d(TAG, "Itinéraire envoyé avec succès au backend")
            } catch (e: Exception) {
                Log.e(TAG, "Échec de l'envoi de l'itinéraire au backend", e)
            }
        }
    }

    /** Réinitialise le panneau après une recherche réussie ou une fermeture. */
    fun reset() {
        originJob?.cancel()
        destinationJob?.cancel()
        _originQuery.value = ""
        _originSuggestions.value = emptyList()
        _originPoint.value = null
        _destinationQuery.value = ""
        _destinationSuggestions.value = emptyList()
        _destinationPoint.value = null
    }
}