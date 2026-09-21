package com.ObservatoireCampus.mobile.viewmodel.station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.model.station.PassageTerDto
import com.ObservatoireCampus.mobile.model.station.StationTerPositionDto
import com.ObservatoireCampus.mobile.network.ErrorContext
import com.ObservatoireCampus.mobile.network.toUserMessage
import com.ObservatoireCampus.mobile.repository.station.StationTerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StationTerViewModel(
    private val repository: StationTerRepository
) : ViewModel() {

    private var allPositions: List<StationTerPositionDto> = emptyList()
    private var passagesJob: Job? = null

    private val _layers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val layers: StateFlow<List<LayerItemUiState>> = _layers

    private val _visiblePositions = MutableStateFlow<List<StationTerPositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<StationTerPositionDto>> = _visiblePositions

    // Bulle (gare cliquee sur la carte)
    private val _selectedStation = MutableStateFlow<StationTerPositionDto?>(null)
    val selectedStation: StateFlow<StationTerPositionDto?> = _selectedStation

    private val _passages = MutableStateFlow<List<PassageTerDto>>(emptyList())
    val passages: StateFlow<List<PassageTerDto>> = _passages

    private val _bubbleLoading = MutableStateFlow(false)
    val bubbleLoading: StateFlow<Boolean> = _bubbleLoading

    // Erreur du chargement des gares (bandeau en haut de la carte)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Erreur du temps reel (affichee DANS la bulle, pas dans le bandeau)
    private val _passagesError = MutableStateFlow<String?>(null)
    val passagesError: StateFlow<String?> = _passagesError

    val masterActive: Boolean
        get() = _layers.value.isNotEmpty() && _layers.value.all { it.visible }

    fun loadStations() {
        viewModelScope.launch {
            try {
                val positions = repository.getPositions()
                allPositions = positions
                _layers.value = listOf(
                    LayerItemUiState(
                        key = "TER",
                        label = "TER",
                        count = positions.size.toLong(),
                        visible = false
                    )
                )
                _error.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.toUserMessage()
            }
        }
    }

    fun toggleType(key: String) {
        _layers.value = _layers.value.map { item ->
            if (item.key == key) item.copy(visible = !item.visible) else item
        }
        recomputeVisiblePositions()
    }

    fun toggleMaster() {
        val shouldActivateAll = !masterActive
        _layers.value = _layers.value.map { it.copy(visible = shouldActivateAll) }
        recomputeVisiblePositions()
    }

    private fun recomputeVisiblePositions() {
        val active = _layers.value.any { it.visible }
        _visiblePositions.value = if (active) allPositions else emptyList()
    }

    // Appele depuis la carte quand l'utilisateur tape sur un marqueur
    fun onStationClicked(station: StationTerPositionDto) {
        _selectedStation.value = station
        loadPassages(station)
    }

    // Bouton "Reessayer" de la bulle
    fun retryPassages() {
        _selectedStation.value?.let { loadPassages(it) }
    }

    private fun loadPassages(station: StationTerPositionDto) {
        passagesJob?.cancel()
        _passages.value = emptyList()
        _passagesError.value = null
        _bubbleLoading.value = true
        passagesJob = viewModelScope.launch {
            try {
                _passages.value = repository.getPassages(station.navitiaId)
                _bubbleLoading.value = false
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _passagesError.value = e.toUserMessage(ErrorContext.REALTIME)
                _bubbleLoading.value = false
            }
        }
    }

    fun closeBubble() {
        passagesJob?.cancel()
        _selectedStation.value = null
        _passages.value = emptyList()
        _passagesError.value = null
        _bubbleLoading.value = false
    }
}