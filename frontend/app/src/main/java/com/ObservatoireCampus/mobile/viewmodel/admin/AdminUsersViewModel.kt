package com.ObservatoireCampus.mobile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.auth.UserDto
import com.ObservatoireCampus.mobile.repository.admin.AdminUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminUsersViewModel(
    private val repository: AdminUserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<Long>>(emptySet())
    val deletingIds: StateFlow<Set<Long>> = _deletingIds.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getUsers()
            result.onSuccess { list ->
                // On ne garde que les comptes USER : il n'y a qu'un seul admin
                // (le compte connecte lui-meme), pas de gestion d'autres admins ici.
                _users.value = list.filter { it.role == "USER" }
            }.onFailure { e ->
                _error.value = e.message ?: "Erreur inconnue"
            }
            _isLoading.value = false
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + id
            val result = repository.deleteUser(id)
            result.onSuccess {
                _users.value = _users.value.filter { it.id != id }
            }.onFailure { e ->
                _error.value = e.message ?: "Erreur lors de la suppression"
            }
            _deletingIds.value = _deletingIds.value - id
        }
    }

    fun clearError() {
        _error.value = null
    }
}

class AdminUsersViewModelFactory(
    private val repository: AdminUserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminUsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminUsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}