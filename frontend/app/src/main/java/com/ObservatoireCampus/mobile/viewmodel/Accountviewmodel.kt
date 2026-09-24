package com.ObservatoireCampus.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.auth.ChangePasswordRequestDto
import com.ObservatoireCampus.mobile.model.auth.UpdateProfileRequestDto
import com.ObservatoireCampus.mobile.model.auth.UserDto
import com.ObservatoireCampus.mobile.repository.AuthRepository
import com.ObservatoireCampus.mobile.repository.NationalityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Les champs du profil modifiables individuellement (username exclu : immuable). */
enum class ProfileField { EMAIL, PHONE, NATIONALITY, RESIDENCE }

data class AccountUiState(
    val user: UserDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null,

    // Edition champ par champ
    val editingField: ProfileField? = null,
    val fieldDraft: String = "",
    val isSavingField: Boolean = false,

    // Section changement de mot de passe
    val isPasswordSectionOpen: Boolean = false,
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isOldPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isChangingPassword: Boolean = false
) {
    val hasMinLength get() = newPassword.length >= 8
    val hasUppercase get() = newPassword.any { it.isUpperCase() }
    val hasDigit get() = newPassword.any { it.isDigit() }
    val hasSpecialChar get() = newPassword.any { !it.isLetterOrDigit() }
    val passwordsMatch get() = newPassword.isNotEmpty() && newPassword == confirmPassword
    val canSubmitPassword get() =
        oldPassword.isNotBlank() && hasMinLength && hasUppercase && hasDigit && hasSpecialChar &&
                passwordsMatch && !isChangingPassword
}

class AccountViewModel(
    private val authRepository: AuthRepository,
    private val languageViewModel: LanguageViewModel,
    private val nationalityRepository: NationalityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    // Liste des nationalités pour la liste déroulante du champ "Nationalité",
    // chargée uniquement depuis le backend (GET /api/nationalities).
    // Vide tant que la réponse n'est pas arrivée (ou si l'appel échoue).
    private val _nationalities = MutableStateFlow<List<String>>(emptyList())
    val nationalities: StateFlow<List<String>> = _nationalities.asStateFlow()

    init {
        loadUser()
        loadNationalities()
    }

    fun loadNationalities() {
        viewModelScope.launch {
            nationalityRepository.getNationalities()
                .onSuccess { list -> _nationalities.value = list }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(user = user, isLoading = false)
                }
                .onFailure { error ->
                    val msg = languageViewModel.translate(error.message ?: "Impossible de charger le profil")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = msg)
                }
        }
    }

    // ---- Edition champ par champ ----

    fun startEditing(field: ProfileField) {
        val current = _uiState.value.user ?: return
        val initialValue = when (field) {
            ProfileField.EMAIL -> current.email
            ProfileField.PHONE -> current.phoneNumber ?: ""
            ProfileField.NATIONALITY -> current.nationality ?: ""
            ProfileField.RESIDENCE -> current.residence ?: ""
        }
        _uiState.value = _uiState.value.copy(
            editingField = field,
            fieldDraft = initialValue,
            error = null,
            successMessage = null
        )
    }

    fun updateDraft(value: String) {
        _uiState.value = _uiState.value.copy(fieldDraft = value)
    }

    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(editingField = null, fieldDraft = "")
    }

    fun saveField() {
        val state = _uiState.value
        val field = state.editingField ?: return
        val user = state.user ?: return

        if (field == ProfileField.EMAIL && state.fieldDraft.isBlank()) {
            viewModelScope.launch {
                _uiState.value = state.copy(error = languageViewModel.translate("L'e-mail ne peut pas être vide"))
            }
            return
        }

        val request = UpdateProfileRequestDto(
            email = if (field == ProfileField.EMAIL) state.fieldDraft.trim() else user.email,
            phoneNumber = if (field == ProfileField.PHONE) state.fieldDraft.trim() else user.phoneNumber,
            nationality = if (field == ProfileField.NATIONALITY) state.fieldDraft.trim() else user.nationality,
            residence = if (field == ProfileField.RESIDENCE) state.fieldDraft.trim() else user.residence
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingField = true, error = null)
            authRepository.updateProfile(request)
                .onSuccess { updatedUser ->
                    val msg = languageViewModel.translate("Informations mises à jour")
                    _uiState.value = _uiState.value.copy(
                        user = updatedUser,
                        isSavingField = false,
                        editingField = null,
                        fieldDraft = "",
                        successMessage = msg
                    )
                }
                .onFailure { error ->
                    val msg = languageViewModel.translate(error.message ?: "Échec de la mise à jour")
                    _uiState.value = _uiState.value.copy(isSavingField = false, error = msg)
                }
        }
    }

    // ---- Changement de mot de passe ----

    fun togglePasswordSection() {
        _uiState.value = _uiState.value.copy(
            isPasswordSectionOpen = !_uiState.value.isPasswordSectionOpen,
            oldPassword = "",
            newPassword = "",
            confirmPassword = "",
            error = null,
            successMessage = null
        )
    }

    fun updateOldPassword(value: String) {
        _uiState.value = _uiState.value.copy(oldPassword = value)
    }

    fun updateNewPassword(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value)
    }

    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value)
    }

    fun toggleOldPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isOldPasswordVisible = !_uiState.value.isOldPasswordVisible)
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isNewPasswordVisible = !_uiState.value.isNewPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun submitPasswordChange() {
        val state = _uiState.value
        if (!state.canSubmitPassword) return

        val request = ChangePasswordRequestDto(
            oldPassword = state.oldPassword,
            newPassword = state.newPassword
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChangingPassword = true, error = null)
            authRepository.changePassword(request)
                .onSuccess {
                    val msg = languageViewModel.translate("Mot de passe modifié avec succès")
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        isPasswordSectionOpen = false,
                        oldPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        successMessage = msg
                    )
                }
                .onFailure { error ->
                    val msg = languageViewModel.translate(error.message ?: "Échec du changement de mot de passe")
                    _uiState.value = _uiState.value.copy(isChangingPassword = false, error = msg)
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

class AccountViewModelFactory(
    private val authRepository: AuthRepository,
    private val languageViewModel: LanguageViewModel,
    private val nationalityRepository: NationalityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(authRepository, languageViewModel, nationalityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}