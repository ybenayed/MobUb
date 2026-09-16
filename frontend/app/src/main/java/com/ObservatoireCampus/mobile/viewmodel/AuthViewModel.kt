package com.ObservatoireCampus.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.auth.AuthResponseDto
import com.ObservatoireCampus.mobile.model.auth.LoginRequestDto
import com.ObservatoireCampus.mobile.model.auth.RegisterRequestDto
import com.ObservatoireCampus.mobile.model.auth.ResetPasswordRequestDto
import com.ObservatoireCampus.mobile.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val authData: AuthResponseDto) : AuthUiState()
    data class ResetPasswordCodeSent(val message: String) : AuthUiState()
    data class ResetPasswordCompleted(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val languageViewModel: LanguageViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // AJOUT : etat global admin/user, lu au demarrage du ViewModel
    private val _isAdmin = MutableStateFlow(authRepository.isAdmin())
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    fun login(username: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val request = LoginRequestDto(username = username, email = email, password = pass)
            val result = authRepository.login(request)

            result.onSuccess {
                _isAdmin.value = authRepository.isAdmin()   // AJOUT : refresh apres login
                _uiState.value = AuthUiState.Success(it)
            }.onFailure { error ->
                val translatedError = languageViewModel.translate(error.message ?: "Erreur de connexion")
                _uiState.value = AuthUiState.Error(translatedError)
            }
        }
    }

    fun register(request: RegisterRequestDto) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(request)

            result.onSuccess {
                _uiState.value = AuthUiState.Success(it)
            }.onFailure { error ->
                val translatedError = languageViewModel.translate(error.message ?: "Erreur lors de la création du compte")
                _uiState.value = AuthUiState.Error(translatedError)
            }
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.requestPasswordReset(email)

            result.onSuccess {
                val successMsg = languageViewModel.translate("Un code a été envoyé à votre e-mail.")
                _uiState.value = AuthUiState.ResetPasswordCodeSent(successMsg)
            }.onFailure { error ->
                val translatedError = languageViewModel.translate(error.message ?: "Erreur lors de l'envoi du code")
                _uiState.value = AuthUiState.Error(translatedError)
            }
        }
    }

    fun confirmPasswordReset(request: ResetPasswordRequestDto) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.confirmPasswordReset(request)

            result.onSuccess {
                val successMsg = languageViewModel.translate("Mot de passe réinitialisé avec succès !")
                _uiState.value = AuthUiState.ResetPasswordCompleted(successMsg)
            }.onFailure { error ->
                val translatedError = languageViewModel.translate(error.message ?: "Code invalide ou expiré")
                _uiState.value = AuthUiState.Error(translatedError)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val languageViewModel: LanguageViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, languageViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}