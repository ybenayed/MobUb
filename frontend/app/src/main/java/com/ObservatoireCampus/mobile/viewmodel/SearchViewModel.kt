package com.ObservatoireCampus.mobile.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.network.toUserMessage
import com.ObservatoireCampus.mobile.repository.SearchRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = SearchRepository()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<SearchResultDto>>(emptyList())
    val suggestions: StateFlow<List<SearchResultDto>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        _error.value = null

        searchJob?.cancel()

        if (newQuery.isBlank() || newQuery.length < 3) {
            _suggestions.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isLoading.value = true
            delay(500)

            try {
                _suggestions.value = repository.searchPlaces(newQuery)
                _isLoading.value = false
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _suggestions.value = emptyList()
                _error.value = e.toUserMessage()
                _isLoading.value = false
            }
        }
    }
}