package com.ObservatoireCampus.mobile.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchHistoryViewModel : ViewModel() {

    private val repository = SearchHistoryRepository()

    private val _items = MutableStateFlow<List<SearchHistoryDto>>(emptyList())
    val items: StateFlow<List<SearchHistoryDto>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<Long>>(emptySet())
    val deletingIds: StateFlow<Set<Long>> = _deletingIds.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getMyHistory()
                .onSuccess { _items.value = it }
                .onFailure { _error.value = it.message ?: "Impossible de charger l'historique" }
            _isLoading.value = false
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + id
            repository.deleteHistoryItem(id)
                .onSuccess {
                    _items.value = _items.value.filterNot { it.id == id }
                }
                .onFailure {
                    _error.value = it.message ?: "Échec de la suppression"
                }
            _deletingIds.value = _deletingIds.value - id
        }
    }
}