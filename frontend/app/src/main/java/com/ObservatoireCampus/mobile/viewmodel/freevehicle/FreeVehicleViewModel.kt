package com.ObservatoireCampus.mobile.viewmodel.freevehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehiclePositionDto
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.network.ErrorContext
import com.ObservatoireCampus.mobile.network.toUserMessage
import com.ObservatoireCampus.mobile.repository.freevehicle.FreeVehicleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreeVehicleViewModel(
    private val repository: FreeVehicleRepository
) : ViewModel() {

    private var allPositions: List<FreeVehiclePositionDto> = emptyList()
    private var detailJob: Job? = null

    private val _layers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val layers: StateFlow<List<LayerItemUiState>> = _layers

    private val _visiblePositions = MutableStateFlow<List<FreeVehiclePositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<FreeVehiclePositionDto>> = _visiblePositions

    // Erreur du chargement / rafraichissement (bandeau en haut de la carte)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var autoRefreshStarted = false

    val masterActive: Boolean
        get() = _layers.value.isNotEmpty() && _layers.value.all { it.visible }

    private val _selectedVehicleId = MutableStateFlow<String?>(null)
    val selectedVehicleId: StateFlow<String?> = _selectedVehicleId

    private val _selectedVehicle = MutableStateFlow<FreeVehicleDetailDto?>(null)
    val selectedVehicle: StateFlow<FreeVehicleDetailDto?> = _selectedVehicle

    private val _bubbleLoading = MutableStateFlow(false)
    val bubbleLoading: StateFlow<Boolean> = _bubbleLoading

    // Erreur du detail d'un vehicule (affichee DANS la bulle)
    private val _detailError = MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError

    fun onVehicleClicked(bikeId: String) {
        android.util.Log.d("FreeVehicle", "bikeId clique = [$bikeId]")
        _selectedVehicleId.value = bikeId
        loadDetail(bikeId)
    }

    fun retryDetail() {
        _selectedVehicleId.value?.let { loadDetail(it) }
    }

    private fun loadDetail(bikeId: String) {
        detailJob?.cancel()
        _selectedVehicle.value = null
        _detailError.value = null
        _bubbleLoading.value = true
        detailJob = viewModelScope.launch {
            try {
                _selectedVehicle.value = repository.getVehicleDetail(bikeId)
                _bubbleLoading.value = false
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailError.value = e.toUserMessage(ErrorContext.REALTIME)
                _bubbleLoading.value = false
            }
        }
    }

    fun closeBubble() {
        detailJob?.cancel()
        _selectedVehicleId.value = null
        _selectedVehicle.value = null
        _detailError.value = null
        _bubbleLoading.value = false
    }

    // Chargement initial : types + comptage
    fun loadStations() {
        viewModelScope.launch {
            try {
                val counts = repository.getTypesCount()
                // On garde la visibilite actuelle si le layer existait deja
                val previousVisibility = _layers.value.associate { it.key to it.visible }

                _layers.value = counts.map { c ->
                    LayerItemUiState(
                        key = c.vehicleTypeId,
                        // On stocke la cle brute. L'UI (Compose) se charge de la traduire.
                        label = c.vehicleTypeId,
                        count = c.count,
                        visible = previousVisibility[c.vehicleTypeId] ?: false
                    )
                }
                _error.value = null
                refreshPositions()
                startAutoRefresh()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.toUserMessage()
            }
        }
    }

    // REFRESH AUTOMATIQUE TOUTES LES 10 SECONDES
    private fun startAutoRefresh() {
        if (autoRefreshStarted) return
        autoRefreshStarted = true
        viewModelScope.launch {
            while (true) {
                delay(10_000)
                refreshPositions()
            }
        }
    }

    private suspend fun refreshPositions() {
        try {
            allPositions = repository.getPositions()
            recomputeVisiblePositions()
            _error.value = null   // le serveur est de retour : le message disparait
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // On garde les dernieres positions connues sur la carte.
            _error.value = e.toUserMessage(ErrorContext.REALTIME)
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
        val activeKeys = _layers.value.filter { it.visible }.map { it.key }.toSet()
        _visiblePositions.value = allPositions.filter { it.vehicleTypeId in activeKeys }
    }
}