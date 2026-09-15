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

    // NOUVEAU : contrôle l'affichage de la bulle "Ma position"
    private val _bubbleVisible = MutableStateFlow(false)
    val bubbleVisible: StateFlow<Boolean> = _bubbleVisible

    // NOUVEAU : précision du dernier fix (mètres), pour affichage dans la bulle
    private val _accuracyMeters = MutableStateFlow<Float?>(null)
    val accuracyMeters: StateFlow<Float?> = _accuracyMeters

    // NOUVEAU : true dès qu'on a demandé/affiché une position (marqueur présent sur la carte)
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive

    /**
     * Appelé par LocationButton à chaque clic.
     * - Si aucune position n'est actuellement affichée -> on la récupère et on l'affiche.
     * - Si une position est déjà affichée -> on efface tout (marqueur + bulle).
     */
    fun toggleLocation() {
        if (_isActive.value) {
            clearLocation()
        } else {
            fetchLocation()
        }
    }

    /** Efface le marqueur et la bulle. */
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
        // La bulle ne s'ouvre plus ici : elle ne doit s'afficher que si l'utilisateur
        // clique sur le marqueur "Ma position" sur la carte (voir onMarkerClicked()).

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

    // NOUVEAU : appelé quand l'utilisateur clique sur son marqueur sur la carte
    fun onMarkerClicked() {
        _bubbleVisible.value = true
    }

    // NOUVEAU : ferme la bulle (bouton close)
    fun closeBubble() {
        _bubbleVisible.value = false
    }

    // NOUVEAU : appelé si la permission est refusée par l'utilisateur
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