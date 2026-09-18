package com.ObservatoireCampus.mobile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import com.ObservatoireCampus.mobile.repository.admin.AdminLegendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminColorsViewModel(
    private val repository: AdminLegendRepository
) : ViewModel() {

    private val _colors = MutableStateFlow<List<InstitutionColorDto>>(emptyList())
    val colors: StateFlow<List<InstitutionColorDto>> = _colors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _deletingInstitutions = MutableStateFlow<Set<String>>(emptySet())
    val deletingInstitutions: StateFlow<Set<String>> = _deletingInstitutions.asStateFlow()

    init {
        loadColors()
    }

    fun loadColors() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getColors()
                .onSuccess { _colors.value = it.sortedBy { c -> c.institution } }
                .onFailure { _error.value = it.message ?: "Erreur inconnue" }
            _isLoading.value = false
        }
    }

    fun save(institution: String, color: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            repository.createOrUpdateColor(institution, color)
                .onSuccess {
                    loadColors()
                    onDone()
                }
                .onFailure { _error.value = it.message ?: "Erreur lors de l'enregistrement" }
            _isSaving.value = false
        }
    }

    fun delete(institution: String) {
        viewModelScope.launch {
            _deletingInstitutions.value = _deletingInstitutions.value + institution
            repository.deleteColor(institution)
                .onSuccess { _colors.value = _colors.value.filter { it.institution != institution } }
                .onFailure { _error.value = it.message ?: "Erreur lors de la suppression" }
            _deletingInstitutions.value = _deletingInstitutions.value - institution
        }
    }
}

class AdminColorsViewModelFactory(
    private val repository: AdminLegendRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminColorsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminColorsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}