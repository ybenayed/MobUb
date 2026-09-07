package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.components.LanguageSelector
import com.ObservatoireCampus.mobile.ui.components.WaypusLogo
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.WaypusAuthBackground
import com.ObservatoireCampus.mobile.ui.theme.WaypusInputBorder
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

data class SignUpFormData(
    val username: String,
    val email: String,
    val phoneNumber: String,
    val nationality: String,
    val residence: String,
    val password: String
)

/**
 * Ecran de creation de compte. Reprend exactement les champs attendus
 * par RegisterRequest cote backend (voir AuthController.register).
 * UI "bete" : la validation finale et l'appel reseau se font dans le ViewModel
 * appelant (prochaine etape), ici on ne fait qu'une pre-validation visuelle
 * du mot de passe pour guider l'utilisateur.
 */
@Composable
fun SignUpScreen(
    languageViewModel: LanguageViewModel,
    onSignUpClick: (SignUpFormData) -> Unit,
    onNavigateBackToLogin: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var residence by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val isPasswordValid = hasMinLength && hasUppercase && hasDigit && hasSpecialChar

    val isFormValid = username.isNotBlank() && email.isNotBlank() &&
            phoneNumber.isNotBlank() && nationality.isNotBlank() &&
            residence.isNotBlank() && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WaypusAuthBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBackToLogin) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour a la connexion",
                    tint = WaypusTextDark
                )
            }
            LanguageSelector(languageViewModel = languageViewModel)
        }

        Spacer(modifier = Modifier.height(4.dp))

        WaypusLogo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            showTagline = false
        )

        Text(
            text = "Creer un compte",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = WaypusTextDark,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = waypusFieldColors(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = waypusFieldColors(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Numero de telephone") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            colors = waypusFieldColors(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = nationality,
            onValueChange = { nationality = it },
            label = { Text("Nationalite") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = waypusFieldColors(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = residence,
            onValueChange = { residence = it },
            label = { Text("Lieu de residence") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = waypusFieldColors(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe",
                        tint = WaypusTextMuted
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = waypusFieldColors(),
            shape = RoundedCornerShape(10.dp)
        )

        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordRequirementsChecklist(
                hasMinLength = hasMinLength,
                hasUppercase = hasUppercase,
                hasDigit = hasDigit,
                hasSpecialChar = hasSpecialChar
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onSignUpClick(
                    SignUpFormData(
                        username = username,
                        email = email,
                        phoneNumber = phoneNumber,
                        nationality = nationality,
                        residence = residence,
                        password = password
                    )
                )
            },
            enabled = isFormValid && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ObcampusPrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Creer mon compte", fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Deja un compte ? ",
                color = WaypusTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Se connecter",
                color = ObcampusPrimary,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onNavigateBackToLogin() }
            )
        }
    }
}

@Composable
private fun PasswordRequirementsChecklist(
    hasMinLength: Boolean,
    hasUppercase: Boolean,
    hasDigit: Boolean,
    hasSpecialChar: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RequirementRow("Au moins 8 caracteres", hasMinLength)
        RequirementRow("Une lettre majuscule", hasUppercase)
        RequirementRow("Un chiffre", hasDigit)
        RequirementRow("Un caractere special", hasSpecialChar)
    }
}

@Composable
private fun RequirementRow(label: String, satisfied: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (satisfied) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (satisfied) ObcampusPrimary else WaypusTextMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (satisfied) ObcampusPrimary else WaypusTextMuted
        )
    }
}

@Composable
private fun waypusFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ObcampusPrimary,
    unfocusedBorderColor = WaypusInputBorder,
    cursorColor = ObcampusPrimary,
    focusedLabelColor = ObcampusPrimary,
    unfocusedLabelColor = WaypusTextMuted
)