package com.ObservatoireCampus.mobile.viewmodel.parking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import com.ObservatoireCampus.mobile.model.parking.ParkingStatusDto
import com.ObservatoireCampus.mobile.network.ErrorContext
import com.ObservatoireCampus.mobile.network.toUserMessage
import com.ObservatoireCampus.mobile.repository.ParkingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(
    private val repository: ParkingRepository
) : ViewModel() {

    private var allPositions: List<ParkingPositionDto> = emptyList()
    private var statusJob: Job? = null

    private val _parkingLayers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val parkingLayers: StateFlow<List<LayerItemUiState>> = _parkingLayers

    private val _visiblePositions = MutableStateFlow<List<ParkingPositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<ParkingPositionDto>> = _visiblePositions

    // Erreur du chargement des parkings (bandeau en haut de la carte)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // --- Selection / bulle de details ---
    private val _selectedParkingId = MutableStateFlow<Long?>(null)
    val selectedParkingId: StateFlow<Long?> = _selectedParkingId

    private val _selectedParkingStatus = MutableStateFlow<ParkingStatusDto?>(null)
    val selectedParkingStatus: StateFlow<ParkingStatusDto?> = _selectedParkingStatus

    private val _bubbleLoading = MutableStateFlow(false)
    val bubbleLoading: StateFlow<Boolean> = _bubbleLoading

    // Erreur de l'etat en direct du parking (affichee DANS la bulle)
    private val _statusError = MutableStateFlow<String?>(null)
    val statusError: StateFlow<String?> = _statusError

    val masterActive: Boolean
        get() = _parkingLayers.value.isNotEmpty() && _parkingLayers.value.all { it.visible }

    fun loadParking() {
        viewModelScope.launch {
            try {
                val positions = repository.getAllPositions()
                val counts = repository.getCountByType()
                allPositions = positions
                _parkingLayers.value = counts.map { count ->
                    LayerItemUiState(key = count.taType, label = count.taType, count = count.count, visible = false)
                }
                _error.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.toUserMessage()
            }
        }
    }

    fun toggleType(key: String) {
        _parkingLayers.value = _parkingLayers.value.map { item ->
            if (item.key == key) item.copy(visible = !item.visible) else item
        }
        recomputeVisiblePositions()
    }

    fun toggleMaster() {
        val shouldActivateAll = !masterActive
        _parkingLayers.value = _parkingLayers.value.map { it.copy(visible = shouldActivateAll) }
        recomputeVisiblePositions()
    }

    private fun recomputeVisiblePositions() {
        val activeKeys = _parkingLayers.value.filter { it.visible }.map { it.key }.toSet()
        _visiblePositions.value = allPositions.filter { it.taType in activeKeys }
    }

    /** Appele quand l'utilisateur tape sur un marqueur parking sur la carte. */
    fun onParkingClicked(id: Long) {
        _selectedParkingId.value = id
        loadStatus(id)
    }

    fun retryStatus() {
        _selectedParkingId.value?.let { loadStatus(it) }
    }

    private fun loadStatus(id: Long) {
        statusJob?.cancel()
        _selectedParkingStatus.value = null
        _statusError.value = null
        _bubbleLoading.value = true
        statusJob = viewModelScope.launch {
            try {
                _selectedParkingStatus.value = repository.getParkingStatus(id)
                _bubbleLoading.value = false
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusError.value = e.toUserMessage(ErrorContext.REALTIME)
                _bubbleLoading.value = false
            }
        }
    }

    fun closeBubble() {
        statusJob?.cancel()
        _selectedParkingId.value = null
        _selectedParkingStatus.value = null
        _statusError.value = null
        _bubbleLoading.value = false
    }
}