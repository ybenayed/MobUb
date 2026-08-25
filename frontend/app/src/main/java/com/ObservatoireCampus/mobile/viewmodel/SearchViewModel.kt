package com.ObservatoireCampus.mobile.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * L'app n'appelle jamais Nominatim directement : elle passe toujours par
 * notre backend Spring (/api/search), via SearchRepository -> GeocodingApi (Retrofit).
 * C'est le backend (GeocodingService.java) qui interroge Nominatim.
 */
class SearchViewModel : ViewModel() {

    private val repository = SearchRepository()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<SearchResultDto>>(emptyList())
    val suggestions: StateFlow<List<SearchResultDto>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery

        // Annuler la recherche précédente si l'utilisateur tape vite (debounce)
        searchJob?.cancel()

        if (newQuery.isBlank() || newQuery.length < 3) {
            _suggestions.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isLoading.value = true
            delay(500) // Attendre 500ms sans saisie avant de lancer la requête

            _suggestions.value = try {
                repository.searchPlaces(newQuery)
            } catch (e: Exception) {
                emptyList()
            }

            _isLoading.value = false
        }
    }
}