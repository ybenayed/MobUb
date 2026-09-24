package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.components.NationalityDropdownField
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.AccountUiState
import com.ObservatoireCampus.mobile.viewmodel.AccountViewModel
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.ProfileField

/** Libelles de l'ecran, traduits via LanguageViewModel (meme pattern que SignUpScreen). */
private data class AccountStrings(
    val title: String = "Mon compte",
    val sectionPersonalInfo: String = "Informations personnelles",
    val labelUsername: String = "Nom d'utilisateur",
    val labelRole: String = "Rôle",
    val labelEmail: String = "E-mail",
    val labelPhone: String = "Téléphone",
    val labelNationality: String = "Nationalité",
    val labelResidence: String = "Résidence",
    val noValue: String = "Non renseigné",
    val edit: String = "Modifier",
    val save: String = "Valider",
    val cancel: String = "Annuler",
    val changePasswordButton: String = "Changer le mot de passe",
    val passwordSectionTitle: String = "Changer / réinitialiser le mot de passe",
    val oldPassword: String = "Ancien mot de passe",
    val newPassword: String = "Nouveau mot de passe",
    val confirmPassword: String = "Confirmer le nouveau mot de passe",
    val showPassword: String = "Afficher le mot de passe",
    val hidePassword: String = "Masquer le mot de passe",
    val reqMinLength: String = "Au moins 8 caractères",
    val reqUppercase: String = "Une majuscule",
    val reqDigit: String = "Un chiffre",
    val reqSpecialChar: String = "Un caractère spécial",
    val passwordsMismatch: String = "Les mots de passe ne correspondent pas"
)

private suspend fun LanguageViewModel.translateAccountStrings(): AccountStrings {
    val base = AccountStrings()
    return AccountStrings(
        title = translate(base.title),
        sectionPersonalInfo = translate(base.sectionPersonalInfo),
        labelUsername = translate(base.labelUsername),
        labelRole = translate(base.labelRole),
        labelEmail = translate(base.labelEmail),
        labelPhone = translate(base.labelPhone),
        labelNationality = translate(base.labelNationality),
        labelResidence = translate(base.labelResidence),
        noValue = translate(base.noValue),
        edit = translate(base.edit),
        save = translate(base.save),
        cancel = translate(base.cancel),
        changePasswordButton = translate(base.changePasswordButton),
        passwordSectionTitle = translate(base.passwordSectionTitle),
        oldPassword = translate(base.oldPassword),
        newPassword = translate(base.newPassword),
        confirmPassword = translate(base.confirmPassword),
        showPassword = translate(base.showPassword),
        hidePassword = translate(base.hidePassword),
        reqMinLength = translate(base.reqMinLength),
        reqUppercase = translate(base.reqUppercase),
        reqDigit = translate(base.reqDigit),
        reqSpecialChar = translate(base.reqSpecialChar),
        passwordsMismatch = translate(base.passwordsMismatch)
    )
}

@Composable
fun AccountScreen(
    accountViewModel: AccountViewModel,
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    val state by accountViewModel.uiState.collectAsState()
    val nationalities by accountViewModel.nationalities.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var strings by remember { mutableStateOf(AccountStrings()) }
    LaunchedEffect(currentLanguage) {
        strings = languageViewModel.translateAccountStrings()
    }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            accountViewModel.clearMessages()
        }
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            accountViewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                languageViewModel = languageViewModel,
                onMenuClick = onBack,
                isBackButton = true
            )
        }
    ) { padding ->
        if (state.isLoading && state.user == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ObcampusPrimary)
            }
            return@Scaffold
        }

        val user = state.user
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Bandeau titre "Mon compte", sous le TopBar
            Text(
                text = strings.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = ObcampusPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (user != null) {
                ProfileHeaderCard(
                    username = user.username,
                    role = user.role,
                    labelUsername = strings.labelUsername,
                    labelRole = strings.labelRole
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = strings.sectionPersonalInfo,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        EditableFieldRow(
                            icon = Icons.Default.Email,
                            label = strings.labelEmail,
                            field = ProfileField.EMAIL,
                            currentValue = user.email,
                            noValueText = strings.noValue,
                            state = state,
                            keyboardType = KeyboardType.Email,
                            editDesc = strings.edit,
                            saveDesc = strings.save,
                            cancelDesc = strings.cancel,
                            onStartEdit = accountViewModel::startEditing,
                            onDraftChange = accountViewModel::updateDraft,
                            onSave = accountViewModel::saveField,
                            onCancel = accountViewModel::cancelEditing
                        )
                        HorizontalDivider()
                        EditableFieldRow(
                            icon = Icons.Default.Phone,
                            label = strings.labelPhone,
                            field = ProfileField.PHONE,
                            currentValue = user.phoneNumber ?: "",
                            noValueText = strings.noValue,
                            state = state,
                            keyboardType = KeyboardType.Phone,
                            editDesc = strings.edit,
                            saveDesc = strings.save,
                            cancelDesc = strings.cancel,
                            onStartEdit = accountViewModel::startEditing,
                            onDraftChange = accountViewModel::updateDraft,
                            onSave = accountViewModel::saveField,
                            onCancel = accountViewModel::cancelEditing
                        )
                        HorizontalDivider()
                        EditableFieldRow(
                            icon = Icons.Default.Public,
                            label = strings.labelNationality,
                            field = ProfileField.NATIONALITY,
                            currentValue = user.nationality ?: "",
                            noValueText = strings.noValue,
                            state = state,
                            editDesc = strings.edit,
                            saveDesc = strings.save,
                            cancelDesc = strings.cancel,
                            onStartEdit = accountViewModel::startEditing,
                            onDraftChange = accountViewModel::updateDraft,
                            onSave = accountViewModel::saveField,
                            onCancel = accountViewModel::cancelEditing,
                            dropdownOptions = nationalities
                        )
                        HorizontalDivider()
                        EditableFieldRow(
                            icon = Icons.Default.LocationOn,
                            label = strings.labelResidence,
                            field = ProfileField.RESIDENCE,
                            currentValue = user.residence ?: "",
                            noValueText = strings.noValue,
                            state = state,
                            editDesc = strings.edit,
                            saveDesc = strings.save,
                            cancelDesc = strings.cancel,
                            onStartEdit = accountViewModel::startEditing,
                            onDraftChange = accountViewModel::updateDraft,
                            onSave = accountViewModel::saveField,
                            onCancel = accountViewModel::cancelEditing,
                            showDivider = false
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                PasswordSection(state = state, strings = strings, viewModel = accountViewModel)

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    username: String,
    role: String,
    labelUsername: String,
    labelRole: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ObcampusPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = ObcampusPrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = role,
                    color = ObcampusPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EditableFieldRow(
    icon: ImageVector,
    label: String,
    field: ProfileField,
    currentValue: String,
    noValueText: String,
    state: AccountUiState,
    keyboardType: KeyboardType = KeyboardType.Text,
    editDesc: String,
    saveDesc: String,
    cancelDesc: String,
    onStartEdit: (ProfileField) -> Unit,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    showDivider: Boolean = true,
    dropdownOptions: List<String>? = null
) {
    val isEditingThis = state.editingField == field

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ObcampusPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))

        if (isEditingThis) {
            if (dropdownOptions != null) {
                NationalityDropdownField(
                    value = state.fieldDraft,
                    onValueChange = onDraftChange,
                    label = label,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSavingField,
                    options = dropdownOptions
                )
            } else {
                OutlinedTextField(
                    value = state.fieldDraft,
                    onValueChange = onDraftChange,
                    label = { Text(label) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    enabled = !state.isSavingField
                )
            }
            if (state.isSavingField) {
                Spacer(Modifier.width(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Check, contentDescription = saveDesc, tint = ObcampusPrimary)
                }
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = cancelDesc, tint = Color.Gray)
                }
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = currentValue.ifBlank { noValueText },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (currentValue.isBlank()) Color.Gray else Color.Unspecified
                )
            }
            IconButton(onClick = { onStartEdit(field) }) {
                Icon(Icons.Default.Edit, contentDescription = "$editDesc $label", tint = ObcampusPrimary)
            }
        }
    }
    if (showDivider) HorizontalDivider()
}

@Composable
private fun PasswordSection(state: AccountUiState, strings: AccountStrings, viewModel: AccountViewModel) {
    if (!state.isPasswordSectionOpen) {
        OutlinedButton(
            onClick = { viewModel.togglePasswordSection() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ObcampusPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(strings.changePasswordButton, fontWeight = FontWeight.Medium)
        }
        return
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = strings.passwordSectionTitle,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PasswordField(
                label = strings.oldPassword,
                value = state.oldPassword,
                isVisible = state.isOldPasswordVisible,
                showDesc = strings.showPassword,
                hideDesc = strings.hidePassword,
                onValueChange = viewModel::updateOldPassword,
                onToggleVisibility = viewModel::toggleOldPasswordVisibility
            )
            Spacer(Modifier.height(8.dp))
            PasswordField(
                label = strings.newPassword,
                value = state.newPassword,
                isVisible = state.isNewPasswordVisible,
                showDesc = strings.showPassword,
                hideDesc = strings.hidePassword,
                onValueChange = viewModel::updateNewPassword,
                onToggleVisibility = viewModel::toggleNewPasswordVisibility
            )

            // Checklist en temps reel des regles de complexite
            Column(modifier = Modifier.padding(top = 8.dp, start = 4.dp)) {
                PasswordRuleRow(strings.reqMinLength, state.hasMinLength)
                PasswordRuleRow(strings.reqUppercase, state.hasUppercase)
                PasswordRuleRow(strings.reqDigit, state.hasDigit)
                PasswordRuleRow(strings.reqSpecialChar, state.hasSpecialChar)
            }

            Spacer(Modifier.height(8.dp))
            PasswordField(
                label = strings.confirmPassword,
                value = state.confirmPassword,
                isVisible = state.isConfirmPasswordVisible,
                showDesc = strings.showPassword,
                hideDesc = strings.hidePassword,
                onValueChange = viewModel::updateConfirmPassword,
                onToggleVisibility = viewModel::toggleConfirmPasswordVisibility,
                isError = state.confirmPassword.isNotEmpty() && !state.passwordsMatch,
                errorText = strings.passwordsMismatch
            )

            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.togglePasswordSection() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) { Text(strings.cancel) }

                Button(
                    onClick = { viewModel.submitPasswordChange() },
                    enabled = state.canSubmitPassword,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ObcampusPrimary)
                ) {
                    if (state.isChangingPassword) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text(strings.save)
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    isVisible: Boolean,
    showDesc: String,
    hideDesc: String,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = { if (isError && errorText != null) Text(errorText, color = MaterialTheme.colorScheme.error) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val icon: ImageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
            IconButton(onClick = onToggleVisibility) {
                Icon(icon, contentDescription = if (isVisible) hideDesc else showDesc)
            }
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordRuleRow(text: String, satisfied: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (satisfied) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (satisfied) Color(0xFF2E7D32) else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = if (satisfied) Color(0xFF2E7D32) else Color.Gray)
    }
}