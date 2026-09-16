package com.ObservatoireCampus.mobile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.admin.AdminStationVDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationVRequestDto
import com.ObservatoireCampus.mobile.repository.admin.AdminInfrastructureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminStationVViewModel(
    private val repository: AdminInfrastructureRepository
) : ViewModel() {

    private val _allStations = mutableListOf<AdminStationVDto>()

    private val _stations = MutableStateFlow<List<AdminStationVDto>>(emptyList())
    val stations: StateFlow<List<AdminStationVDto>> = _stations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<Long>>(emptySet())
    val deletingIds: StateFlow<Set<Long>> = _deletingIds.asStateFlow()

    // État de la recherche
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadStations()
    }

    fun loadStations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getStationsV()
                .onSuccess { list ->
                    _allStations.clear()
                    _allStations.addAll(list.sortedBy { it.id })
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
                        it.stationId.contains(query, ignoreCase = true)
            }
        }
    }

    fun save(existingId: Long?, request: AdminStationVRequestDto, onDone: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            val result = if (existingId == null) {
                repository.createStationV(request)
            } else {
                repository.updateStationV(existingId, request)
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
            repository.deleteStationV(id)
                .onSuccess {
                    _allStations.removeAll { it.id == id }
                    filterStations(_searchQuery.value)
                }
                .onFailure { _error.value = it.message ?: "Erreur lors de la suppression" }
            _deletingIds.value = _deletingIds.value - id
        }
    }
}

class AdminStationVViewModelFactory(
    private val repository: AdminInfrastructureRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminStationVViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminStationVViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}