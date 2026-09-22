package com.ObservatoireCampus.mobile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.BatimentDto
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import com.ObservatoireCampus.mobile.network.toUserMessage
import com.ObservatoireCampus.mobile.repository.MapRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MapRepository(application.applicationContext)

    private val _campusList = MutableStateFlow<List<CampusDto>>(emptyList())
    val campusList: StateFlow<List<CampusDto>> = _campusList

    private val _batimentList = MutableStateFlow<List<BatimentDto>>(emptyList())
    val batimentList: StateFlow<List<BatimentDto>> = _batimentList

    private val _legendList = MutableStateFlow<List<InstitutionColorDto>>(emptyList())
    val legendList: StateFlow<List<InstitutionColorDto>> = _legendList

    // Deux sources d'erreur, regroupees en une seule (sans doublon) pour le bandeau
    private val _campusError = MutableStateFlow<String?>(null)
    private val _legendError = MutableStateFlow<String?>(null)

    val error: StateFlow<String?> = combine(_campusError, _legendError) { campus, legend ->
        listOfNotNull(campus, legend).distinct().joinToString(" | ").ifEmpty { null }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        loadCampus()
        loadLegend()
    }

    fun loadLegend() {
        viewModelScope.launch {
            try {
                _legendList.value = repository.getInstitutionColors()
                _legendError.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MapViewModel", ">>> ERREUR legende: ${e.message}", e)
                _legendError.value = e.toUserMessage()
            }
        }
    }

    fun loadCampus() {
        viewModelScope.launch {
            try {
                val result = repository.getCampus()
                _campusList.value = result

                // Execution en parallele de toutes les requetes de batiments
                val allBatiments = coroutineScope {
                    result.map { campus ->
                        async { repository.getBatiments(campus.id) }
                    }.awaitAll().flatten()
                }

                _batimentList.value = allBatiments
                _campusError.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MapViewModel", ">>> ERREUR campus/batiments: ${e.message}", e)
                _campusError.value = e.toUserMessage()
            }
        }
    }

    fun loadBatiments(campusId: Long) {
        viewModelScope.launch {
            try {
                _batimentList.value = repository.getBatiments(campusId)
                _campusError.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MapViewModel", ">>> ERREUR batiments: ${e.message}", e)
                _campusError.value = e.toUserMessage()
            }
        }
    }
}