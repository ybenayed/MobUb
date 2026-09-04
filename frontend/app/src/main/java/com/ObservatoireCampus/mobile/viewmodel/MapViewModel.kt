package com.ObservatoireCampus.mobile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.repository.MapRepository
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.BatimentDto
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import  kotlinx.coroutines.awaitAll

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MapRepository(application.applicationContext)

    private val _campusList = MutableStateFlow<List<CampusDto>>(emptyList())
    val campusList: StateFlow<List<CampusDto>> = _campusList

    private val _batimentList = MutableStateFlow<List<BatimentDto>>(emptyList())
    val batimentList: StateFlow<List<BatimentDto>> = _batimentList

    private val _legendList = MutableStateFlow<List<InstitutionColorDto>>(emptyList())
    val legendList: StateFlow<List<InstitutionColorDto>> = _legendList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    init {
        loadCampus()
        loadLegend()
    }

    fun loadLegend() {
        viewModelScope.launch {
            try {
                val legend = repository.getInstitutionColors()
                _legendList.value = legend
            } catch (e: Exception) {
                Log.e("MapViewModel", ">>> ERREUR légende: ${e.message}", e)
            }
        }
    }

    fun loadCampus() {
        viewModelScope.launch {
            try {
                val result = repository.getCampus()
                _campusList.value = result
                _error.value = null

                // Exécution en parallèle de toutes les requêtes de bâtiments avec async/awaitAll
                val allBatiments = kotlinx.coroutines.coroutineScope {
                    result.map { campus ->
                        async { repository.getBatiments(campus.id) }
                    }.awaitAll().flatten()
                }

                _batimentList.value = allBatiments
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadBatiments(campusId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.getBatiments(campusId)
                _batimentList.value = result
            } catch (e: Exception) {
                Log.e("MapViewModel", ">>> ERREUR bâtiments: ${e.message}", e)
            }
        }
    }
}