package com.ObservatoireCampus.mobile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.admin.AdminParkingDto
import com.ObservatoireCampus.mobile.model.admin.AdminParkingRequestDto
import com.ObservatoireCampus.mobile.repository.admin.AdminInfrastructureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminParkingViewModel(
    private val repository: AdminInfrastructureRepository
) : ViewModel() {

    private val _allParkings = mutableListOf<AdminParkingDto>()

    private val _parkings = MutableStateFlow<List<AdminParkingDto>>(emptyList())
    val parkings: StateFlow<List<AdminParkingDto>> = _parkings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<Long>>(emptySet())
    val deletingIds: StateFlow<Set<Long>> = _deletingIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadParkings()
    }

    fun loadParkings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getParkings()
                .onSuccess { list ->
                    _allParkings.clear()
                    _allParkings.addAll(list.sortedBy { it.id })
                    filterParkings(_searchQuery.value)
                }
                .onFailure { _error.value = it.message ?: "Erreur inconnue" }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterParkings(query)
    }

    private fun filterParkings(query: String) {
        if (query.isBlank()) {
            _parkings.value = _allParkings.toList()
        } else {
            _parkings.value = _allParkings.filter {
                it.nom?.contains(query, ignoreCase = true) == true ||
                        it.ident.contains(query, ignoreCase = true)
            }
        }
    }

    fun nextAutoIdent(): String {
        val prefix = "manual:parking:"
        val maxExisting = _allParkings
            .mapNotNull { it.ident.removePrefix(prefix).toIntOrNull() }
            .maxOrNull() ?: 0
        return "$prefix${maxExisting + 1}"
    }

    fun save(existingId: Long?, request: AdminParkingRequestDto, onDone: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            val result = if (existingId == null) {
                repository.createParking(request)
            } else {
                repository.updateParking(existingId, request)
            }
            result.onSuccess {
                loadParkings()
                onDone()
            }.onFailure {
                _error.value = it.message ?: "Erreur lors de l'enregistrement"
            }
            _isSaving.value = false
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + id
            repository.deleteParking(id)
                .onSuccess {
                    _allParkings.removeAll { it.id == id }
                    filterParkings(_searchQuery.value)
                }
                .onFailure { _error.value = it.message ?: "Erreur lors de la suppression" }
            _deletingIds.value = _deletingIds.value - id
        }
    }
}

class AdminParkingViewModelFactory(
    private val repository: AdminInfrastructureRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminParkingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminParkingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}