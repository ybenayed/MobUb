package com.ObservatoireCampus.mobile.viewmodel.itinerary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.CURRENT_LOCATION_MARKER
import com.ObservatoireCampus.mobile.model.search.ItineraryFilters
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.model.search.ItinerarySortOption
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.model.search.TransportModeUi
import com.ObservatoireCampus.mobile.model.search.deduplicatedByModeSequence
import com.ObservatoireCampus.mobile.model.search.sortedByOption
import com.ObservatoireCampus.mobile.network.ErrorContext
import com.ObservatoireCampus.mobile.network.toUserMessage
import com.ObservatoireCampus.mobile.repository.ItineraryRepository
import com.ObservatoireCampus.mobile.repository.SearchHistoryRepository
import com.ObservatoireCampus.mobile.repository.SearchRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ItineraryViewModel"

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

    // ---------- FILTRES ----------
    private val _filters = MutableStateFlow(ItineraryFilters())
    val filters: StateFlow<ItineraryFilters> = _filters.asStateFlow()

    // ---------- RESULTATS DE RECHERCHE ----------
    private val _itineraryOptions = MutableStateFlow<List<ItineraryOptionDto>>(emptyList())
    val itineraryOptions: StateFlow<List<ItineraryOptionDto>> = _itineraryOptions.asStateFlow()

    val sortedItineraryOptions: StateFlow<List<ItineraryOptionDto>> =
        combine(_itineraryOptions, _filters) { options, filters ->
            options.sortedByOption(filters.sortBy)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedItinerary = MutableStateFlow<ItineraryOptionDto?>(null)
    val selectedItinerary: StateFlow<ItineraryOptionDto?> = _selectedItinerary.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Message affiche sous le formulaire : "Aucun itineraire trouve" OU une erreur reseau/serveur
    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    // ---------- POPUP DE DETAILS ----------
    private val _detailsItinerary = MutableStateFlow<ItineraryOptionDto?>(null)
    val detailsItinerary: StateFlow<ItineraryOptionDto?> = _detailsItinerary.asStateFlow()

    private val searchHistoryRepository = SearchHistoryRepository()

    fun showItineraryDetails(option: ItineraryOptionDto) {
        _detailsItinerary.value = option
    }

    fun dismissItineraryDetails() {
        _detailsItinerary.value = null
    }

    // ---------- ENREGISTREMENT DANS L'HISTORIQUE ----------
    private val _savedHistorySignatures = MutableStateFlow<Set<Int>>(emptySet())
    val savedHistorySignatures: StateFlow<Set<Int>> = _savedHistorySignatures.asStateFlow()

    private val _savingHistorySignatures = MutableStateFlow<Set<Int>>(emptySet())
    val savingHistorySignatures: StateFlow<Set<Int>> = _savingHistorySignatures.asStateFlow()

    private val _saveHistoryMessage = MutableStateFlow<String?>(null)
    val saveHistoryMessage: StateFlow<String?> = _saveHistoryMessage.asStateFlow()

    fun saveToHistory(option: ItineraryOptionDto) {
        val origin = _originPoint.value
        val destination = _destinationPoint.value
        if (origin == null || destination == null) return

        val signature = option.hashCode()
        if (signature in _savedHistorySignatures.value || signature in _savingHistorySignatures.value) return

        viewModelScope.launch {
            _savingHistorySignatures.value = _savingHistorySignatures.value + signature
            val result = searchHistoryRepository.saveToHistory(origin, destination, option)
            result.onSuccess {
                _savedHistorySignatures.value = _savedHistorySignatures.value + signature
                _saveHistoryMessage.value = "Itinéraire enregistré dans l'historique"
            }.onFailure {
                _saveHistoryMessage.value = "Échec de l'enregistrement : ${it.toUserMessage()}"
            }
            _savingHistorySignatures.value = _savingHistorySignatures.value - signature
        }
    }

    fun clearSaveHistoryMessage() {
        _saveHistoryMessage.value = null
    }

    // ----- Origine -----
    fun onOriginQueryChanged(newQuery: String) {
        _originQuery.value = newQuery
        _originPoint.value = null
        _searchError.value = null
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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _searchError.value = e.toUserMessage()
                emptyList()
            }
            _originLoading.value = false
        }
    }

    fun selectOrigin(result: SearchResultDto) {
        originJob?.cancel()
        _originLoading.value = false
        _originPoint.value = result
        _originQuery.value = result.name
        _originSuggestions.value = emptyList()
    }

    fun setOriginToMyLocation(latitude: Double, longitude: Double, displayLabel: String = "Ma position") {
        originJob?.cancel()
        _originLoading.value = false
        _originPoint.value = SearchResultDto(
            name = CURRENT_LOCATION_MARKER,   // <-- toujours le meme marqueur, jamais traduit
            latitude = latitude,
            longitude = longitude,
            subtitle = ""
        )
        _originQuery.value = displayLabel     // <-- ce que l'utilisateur voit dans le champ, traduit en direct
        _originSuggestions.value = emptyList()
    }

    // ----- Destination -----
    fun onDestinationQueryChanged(newQuery: String) {
        _destinationQuery.value = newQuery
        _destinationPoint.value = null
        _searchError.value = null
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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _searchError.value = e.toUserMessage()
                emptyList()
            }
            _destinationLoading.value = false
        }
    }

    fun selectDestination(result: SearchResultDto) {
        destinationJob?.cancel()
        _destinationLoading.value = false
        _destinationPoint.value = result
        _destinationQuery.value = result.name
        _destinationSuggestions.value = emptyList()
    }

    fun setDestinationToMyLocation(latitude: Double, longitude: Double, displayLabel: String = "Ma position") {
        destinationJob?.cancel()
        _destinationLoading.value = false
        _destinationPoint.value = SearchResultDto(
            name = CURRENT_LOCATION_MARKER,
            latitude = latitude,
            longitude = longitude,
            subtitle = ""
        )
        _destinationQuery.value = displayLabel
        _destinationSuggestions.value = emptyList()
    }

    // ----- Filtres -----
    fun toggleMode(mode: TransportModeUi) {
        val current = _filters.value.modes
        val updated = if (mode in current) current - mode else current + mode
        if (updated.isNotEmpty()) {
            _filters.value = _filters.value.copy(modes = updated)
        }
    }

    fun updateTimeFilter(date: String?, time: String?, arriveBy: Boolean) {
        _filters.value = _filters.value.copy(date = date, time = time, arriveBy = arriveBy)
    }

    fun updateSortOption(option: ItinerarySortOption) {
        _filters.value = _filters.value.copy(sortBy = option)
    }

    fun resetFilters() {
        _filters.value = ItineraryFilters()
    }

    // ----- Recherche -----

    /**
     * PMR retire : il n'y a plus que 2 branches (velo perso vs le reste),
     * computeAccessibleItinerary n'existe plus.
     */
    fun submitItinerary() {
        val origin = _originPoint.value
        val destination = _destinationPoint.value

        if (origin == null || destination == null) {
            Log.w(TAG, "submitItinerary() annule : origin=$origin destination=$destination")
            return
        }

        val currentFilters = _filters.value
        Log.d(TAG, "Envoi vers le backend -> origin=$origin destination=$destination filters=$currentFilters")

        _itineraryOptions.value = emptyList()
        _selectedItinerary.value = null
        _searchError.value = null
        _detailsItinerary.value = null
        _savedHistorySignatures.value = emptySet()
        _savingHistorySignatures.value = emptySet()

        viewModelScope.launch {
            _isSearching.value = true
            try {
                val options = if (currentFilters.isPersonalBikeOnly) {
                    itineraryRepository.computeBicycleItineraries(origin, destination, currentFilters)
                } else {
                    itineraryRepository.computeItinerary(origin, destination, currentFilters)
                }

                // Une seule proposition par sequence de modes (pas de doublons).
                _itineraryOptions.value = options.deduplicatedByModeSequence()

                if (options.isEmpty()) {
                    // Pas une panne : le serveur a repondu, mais aucun trajet n'existe.
                    _searchError.value = "Aucun itineraire trouve"
                }
                Log.d(TAG, "${options.size} itineraire(s) recu(s) du backend, ${_itineraryOptions.value.size} apres dedup")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Echec du calcul d'itineraire", e)
                // Message adapte au type d'erreur (pas de connexion, serveur en panne, delai depasse...)
                _searchError.value = e.toUserMessage(ErrorContext.ITINERARY)
            } finally {
                _isSearching.value = false
            }
        }
    }

    /** Appele quand l'utilisateur choisit "Voir sur la carte" dans la popup de details. */
    fun selectItinerary(option: ItineraryOptionDto) {
        _selectedItinerary.value = option
    }

    fun clearResults() {
        _itineraryOptions.value = emptyList()
        _selectedItinerary.value = null
        _searchError.value = null
        _detailsItinerary.value = null
        _savedHistorySignatures.value = emptySet()
        _savingHistorySignatures.value = emptySet()
    }

    fun reset() {
        originJob?.cancel()
        destinationJob?.cancel()
        _originLoading.value = false
        _destinationLoading.value = false
        _originQuery.value = ""
        _originSuggestions.value = emptyList()
        _originPoint.value = null
        _destinationQuery.value = ""
        _destinationSuggestions.value = emptyList()
        _destinationPoint.value = null
        resetFilters()
        clearResults()
    }

    fun resetAllFields() {
        reset()
    }
}