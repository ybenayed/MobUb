package com.ObservatoireCampus.mobile.viewmodel.location

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

sealed class LocationUiState {
    object Idle : LocationUiState()
    object Loading : LocationUiState()
    data class Success(val point: GeoPoint) : LocationUiState()
    data class Error(val message: String) : LocationUiState()
}

class LocationViewModel(
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation

    private val _locationState = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val locationState: StateFlow<LocationUiState> = _locationState

    private val _bubbleVisible = MutableStateFlow(false)
    val bubbleVisible: StateFlow<Boolean> = _bubbleVisible

    private val _accuracyMeters = MutableStateFlow<Float?>(null)
    val accuracyMeters: StateFlow<Float?> = _accuracyMeters

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive

    fun toggleLocation() {
        if (_isActive.value) {
            clearLocation()
        } else {
            fetchLocation()
        }
    }
    fun clearLocation() {
        _isActive.value = false
        _bubbleVisible.value = false
        _userLocation.value = null
        _accuracyMeters.value = null
        _locationState.value = LocationUiState.Idle
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        _isActive.value = true
        _locationState.value = LocationUiState.Loading

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val point = GeoPoint(location.latitude, location.longitude)
                    _userLocation.value = point
                    _accuracyMeters.value = if (location.hasAccuracy()) location.accuracy else null
                    _locationState.value = LocationUiState.Success(point)
                } else {
                    requestFreshLocation()
                }
            }
            .addOnFailureListener {
                _locationState.value = LocationUiState.Error("Impossible de récupérer la position")
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation() {
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setDurationMillis(10_000)
            .build()

        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val point = GeoPoint(location.latitude, location.longitude)
                    _userLocation.value = point
                    _accuracyMeters.value = if (location.hasAccuracy()) location.accuracy else null
                    _locationState.value = LocationUiState.Success(point)
                } else {
                    _locationState.value =
                        LocationUiState.Error("Position indisponible. Vérifie que le GPS est activé.")
                }
            }
            .addOnFailureListener {
                _locationState.value = LocationUiState.Error("Erreur GPS : ${it.localizedMessage}")
            }
    }

    fun onMarkerClicked() {
        _bubbleVisible.value = true
    }

    fun closeBubble() {
        _bubbleVisible.value = false
    }
    fun onPermissionDenied() {
        _isActive.value = true
        _bubbleVisible.value = true
        _locationState.value =
            LocationUiState.Error("Permission de localisation refusée.")
    }

    fun resetError() {
        _locationState.value = LocationUiState.Idle
    }
}