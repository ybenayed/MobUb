package com.ObservatoireCampus.mobile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.admin.AdminStationTBDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTBRequestDto
import com.ObservatoireCampus.mobile.repository.admin.AdminInfrastructureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel partage par les onglets Bus et Tram : StationTB est une seule
 * entite cote backend, distinguee par le champ "mode". On filtre ici
 * sur le mode fixe passe au constructeur ("BUS" ou "TRAM").
 */
class AdminStationTBViewModel(
    private val repository: AdminInfrastructureRepository,
    private val mode: String
) : ViewModel() {

    private val _allStations = mutableListOf<AdminStationTBDto>()

    private val _stations = MutableStateFlow<List<AdminStationTBDto>>(emptyList())
    val stations: StateFlow<List<AdminStationTBDto>> = _stations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<Long>>(emptySet())
    val deletingIds: StateFlow<Set<Long>> = _deletingIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadStations()
    }

    fun loadStations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getStationsTB()
                .onSuccess { list ->
                    _allStations.clear()
                    // Ne garde que les stations du mode de cet onglet (BUS ou TRAM)
                    _allStations.addAll(list.filter { it.mode == mode }.sortedBy { it.id })
                    filterStations(_searchQuery.value)
                }
                .onFailure { _error.value = it.message ?: "Erreur inconnue" }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterStations(query)
    }

    private fun filterStations(query: String) {
        if (query.isBlank()) {
            _stations.value = _allStations.toList()
        } else {
            _stations.value = _allStations.filter {
                it.nom?.contains(query, ignoreCase = true) == true ||
                        it.stopId.contains(query, ignoreCase = true)
            }
        }
    }

    /** Genere un identifiant "maison" unique pour une nouvelle station de ce mode. */
    fun nextAutoStopId(): String {
        val prefix = "manual:tb:"
        val maxExisting = _allStations
            .mapNotNull { it.stopId.removePrefix(prefix).toIntOrNull() }
            .maxOrNull() ?: 0
        return "$prefix${maxExisting + 1}"
    }

    fun save(existingId: Long?, request: AdminStationTBRequestDto, onDone: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            val requestWithMode = request.copy(mode = mode)
            val result = if (existingId == null) {
                repository.createStationTB(requestWithMode)
            } else {
                repository.updateStationTB(existingId, requestWithMode)
            }
            result.onSuccess {
                loadStations()
                onDone()
            }.onFailure {
                _error.value = it.message ?: "Erreur lors de l'enregistrement"
            }
            _isSaving.value = false
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + id
            repository.deleteStationTB(id)
                .onSuccess {
                    _allStations.removeAll { it.id == id }
                    filterStations(_searchQuery.value)
                }
                .onFailure { _error.value = it.message ?: "Erreur lors de la suppression" }
            _deletingIds.value = _deletingIds.value - id
        }
    }
}

class AdminStationTBViewModelFactory(
    private val repository: AdminInfrastructureRepository,
    private val mode: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminStationTBViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminStationTBViewModel(repository, mode) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}