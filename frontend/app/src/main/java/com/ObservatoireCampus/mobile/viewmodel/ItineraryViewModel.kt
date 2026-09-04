package com.ObservatoireCampus.mobile.viewmodel.itinerary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.ItineraryFilters
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.model.search.ItinerarySortOption
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.model.search.TransportModeUi
import com.ObservatoireCampus.mobile.model.search.sortedByOption
import com.ObservatoireCampus.mobile.repository.ItineraryRepository
import com.ObservatoireCampus.mobile.repository.SearchRepository
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

/**
 * Gere les 2 champs (origine / destination) + les filtres du panneau "Itineraire".
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

    // ---------- FILTRES ----------
    private val _filters = MutableStateFlow(ItineraryFilters())
    val filters: StateFlow<ItineraryFilters> = _filters.asStateFlow()

    // ---------- RESULTATS DE RECHERCHE ----------
    // Contient TOUJOURS la liste complete et brute renvoyee par le backend
    // (aucune troncature : voir MAX_ITINERARIES_REQUESTED cote backend, qui est
    // un plafond de *demande* a OTP, pas un filtre de reponse).
    private val _itineraryOptions = MutableStateFlow<List<ItineraryOptionDto>>(emptyList())
    val itineraryOptions: StateFlow<List<ItineraryOptionDto>> = _itineraryOptions.asStateFlow()

    /**
     * Options triees selon filters.sortBy, recalculees automatiquement des que
     * les resultats OU le tri choisi changent. Aucun appel reseau ici : c'est
     * exactement ce qui alimente la liste deroulante de propositions dans l'UI.
     */
    val sortedItineraryOptions: StateFlow<List<ItineraryOptionDto>> =
        combine(_itineraryOptions, _filters) { options, filters ->
            options.sortedByOption(filters.sortBy)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedItinerary = MutableStateFlow<ItineraryOptionDto?>(null)
    val selectedItinerary: StateFlow<ItineraryOptionDto?> = _selectedItinerary.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    // ----- Origine -----
    fun onOriginQueryChanged(newQuery: String) {
        _originQuery.value = newQuery
        _originPoint.value = null // toute frappe manuelle invalide la selection precedente
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

    /** Appele par le bouton "cible" du champ origine. */
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

    /** Appele par le bouton "cible" du champ destination. */
    fun setDestinationToMyLocation(latitude: Double, longitude: Double, label: String = "Ma position") {
        destinationJob?.cancel()
        _destinationPoint.value = SearchResultDto(name = label, latitude = latitude, longitude = longitude, subtitle = "")
        _destinationQuery.value = label
        _destinationSuggestions.value = emptyList()
    }

    // ----- Filtres -----

    /** Active/desactive un mode de transport. Refuse de vider entierement la selection. */
    fun toggleMode(mode: TransportModeUi) {
        val current = _filters.value.modes
        val updated = if (mode in current) current - mode else current + mode
        if (updated.isNotEmpty()) {
            _filters.value = _filters.value.copy(modes = updated)
        }
    }

    /** date/time null = "maintenant". arriveBy=true -> "je veux arriver a". */
    fun updateTimeFilter(date: String?, time: String?, arriveBy: Boolean) {
        _filters.value = _filters.value.copy(date = date, time = time, arriveBy = arriveBy)
    }

    fun toggleWheelchair() {
        _filters.value = _filters.value.copy(wheelchair = !_filters.value.wheelchair)
    }

    fun updateSortOption(option: ItinerarySortOption) {
        _filters.value = _filters.value.copy(sortBy = option)
    }

    /** Remet les filtres a leur valeur par defaut (sans toucher origine/destination). */
    fun resetFilters() {
        _filters.value = ItineraryFilters()
    }

    // ----- Recherche -----

    /**
     * Envoie origine + destination + filtres courants au backend et stocke les
     * options recues dans _itineraryOptions.
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

        viewModelScope.launch {
            _isSearching.value = true
            try {
                val options = when {
                    currentFilters.wheelchair ->
                        itineraryRepository.computeAccessibleItinerary(origin, destination, currentFilters)

                    currentFilters.isPersonalBikeOnly ->
                        itineraryRepository.computeBicycleItineraries(origin, destination, currentFilters)

                    else ->
                        itineraryRepository.computeItinerary(origin, destination, currentFilters)
                }

                _itineraryOptions.value = options
                if (options.isEmpty()) {
                    _searchError.value = "Aucun itineraire trouve"
                }
                Log.d(TAG, "${options.size} itineraire(s) recu(s) du backend")
            } catch (e: Exception) {
                Log.e(TAG, "Echec du calcul d'itineraire", e)
                _searchError.value = "Erreur reseau, reessayez"
            } finally {
                _isSearching.value = false
            }
        }
    }

    /** Appele quand l'utilisateur choisit une option dans ItineraryResultsList. */
    fun selectItinerary(option: ItineraryOptionDto) {
        _selectedItinerary.value = option
    }

    /** Efface les resultats de recherche (sans toucher aux champs origine/destination/filtres). */
    fun clearResults() {
        _itineraryOptions.value = emptyList()
        _selectedItinerary.value = null
        _searchError.value = null
    }

    /** Reinitialise tout le panneau (origine, destination, filtres, resultats). */
    fun reset() {
        originJob?.cancel()
        destinationJob?.cancel()
        _originQuery.value = ""
        _originSuggestions.value = emptyList()
        _originPoint.value = null
        _destinationQuery.value = ""
        _destinationSuggestions.value = emptyList()
        _destinationPoint.value = null
        resetFilters()
        clearResults()
    }

    /** Alias appele par `onResetClick` dans l'interface Composable. */
    fun resetAllFields() {
        reset()
    }
}