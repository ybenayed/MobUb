package com.ObservatoireCampus.mobile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.admin.AdminDashboardStatsDto
import com.ObservatoireCampus.mobile.repository.admin.AdminUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminStatsViewModel(
    private val repository: AdminUserRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<AdminDashboardStatsDto?>(null)
    val stats: StateFlow<AdminDashboardStatsDto?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getDashboardStats()
                .onSuccess { _stats.value = it }
                .onFailure { _error.value = it.message ?: "Erreur inconnue" }
            _isLoading.value = false
        }
    }
}

class AdminStatsViewModelFactory(
    private val repository: AdminUserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminStatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}