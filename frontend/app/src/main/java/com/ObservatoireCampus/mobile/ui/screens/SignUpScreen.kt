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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import  androidx.compose.ui.zIndex
import androidx.compose.ui.viewinterop.AndroidView
import com.ObservatoireCampus.mobile.ui.components.LanguageSelector
import com.ObservatoireCampus.mobile.ui.components.MobUbLogo
import com.ObservatoireCampus.mobile.ui.components.NationalityDropdownField
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.WaypusInputBorder
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

data class SignUpFormData(
    val username: String,
    val email: String,
    val phoneNumber: String,
    val nationality: String,
    val residence: String,
    val password: String
)

private data class SignUpStrings(
    val back: String = "Retour a la connexion",
    val title: String = "Creer un compte",
    val username: String = "Nom d'utilisateur",
    val email: String = "Email",
    val phoneNumber: String = "Numero de telephone",
    val nationality: String = "Nationalite",
    val residence: String = "Lieu de residence",
    val password: String = "Mot de passe",
    val showPassword: String = "Afficher le mot de passe",
    val hidePassword: String = "Masquer le mot de passe",
    val reqMinLength: String = "Au moins 8 caracteres",
    val reqUppercase: String = "Une lettre majuscule",
    val reqDigit: String = "Un chiffre",
    val reqSpecialChar: String = "Un caractere special",
    val submit: String = "Creer mon compte",
    val alreadyHaveAccount: String = "Deja un compte ? ",
    val login: String = "Se connecter"
)

private suspend fun LanguageViewModel.translateSignUpStrings(): SignUpStrings {
    val base = SignUpStrings()
    return SignUpStrings(
        back = translate(base.back),
        title = translate(base.title),
        username = translate(base.username),
        email = translate(base.email),
        phoneNumber = translate(base.phoneNumber),
        nationality = translate(base.nationality),
        residence = translate(base.residence),
        password = translate(base.password),
        showPassword = translate(base.showPassword),
        hidePassword = translate(base.hidePassword),
        reqMinLength = translate(base.reqMinLength),
        reqUppercase = translate(base.reqUppercase),
        reqDigit = translate(base.reqDigit),
        reqSpecialChar = translate(base.reqSpecialChar),
        submit = translate(base.submit),
        alreadyHaveAccount = translate(base.alreadyHaveAccount),
        login = translate(base.login)
    )
}

@Composable
fun SignUpScreen(
    languageViewModel: LanguageViewModel,
    onSignUpClick: (SignUpFormData) -> Unit,
    onNavigateBackToLogin: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    nationalities: List<String> = emptyList()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var residence by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var strings by remember { mutableStateOf(SignUpStrings()) }
    LaunchedEffect(currentLanguage) {
        strings = languageViewModel.translateSignUpStrings()
    }

    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val isPasswordValid = hasMinLength && hasUppercase && hasDigit && hasSpecialChar

    val isFormValid = username.isNotBlank() && email.isNotBlank() &&
            phoneNumber.isNotBlank() && nationality.isNotBlank() &&
            residence.isNotBlank() && isPasswordValid

    val defaultCenter = remember { GeoPoint(44.8069, -0.5959) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    DisposableEffect(Unit) {
        onDispose { mapViewRef?.onDetach() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- Fond carte OSM ---
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                Configuration.getInstance().userAgentValue = ctx.packageName
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(false)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    minZoomLevel = 5.0
                    maxZoomLevel = 19.0

                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        android.view.ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            controller.setZoom(13.0)
                            controller.setCenter(defaultCenter)
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })

                    setOnTouchListener { _, _ -> true }
                }.also { mapViewRef = it }
            },
            update = { mapView ->
                mapView.controller.setCenter(defaultCenter)
            }
        )

        // Voile sombre pour le contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.10f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.TopCenter)
                .zIndex(100f), //
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBackToLogin) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = strings.back,
                    tint = WaypusTextDark
                )
            }
            LanguageSelector(languageViewModel = languageViewModel)
        }

        // Formulaire d'inscription
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(top = 64.dp, bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.65f))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                MobUbLogo(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    showTagline = false
                )

                Text(
                    text = strings.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = WaypusTextDark,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 24.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(strings.username) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = waypusFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(strings.email) },
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
                    label = { Text(strings.phoneNumber) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = waypusFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                NationalityDropdownField(
                    value = nationality,
                    onValueChange = { nationality = it },
                    label = strings.nationality,
                    modifier = Modifier.fillMaxWidth(),
                    options = nationalities,
                    colors = waypusFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = residence,
                    onValueChange = { residence = it },
                    label = { Text(strings.residence) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = waypusFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(strings.password) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) strings.hidePassword else strings.showPassword,
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
                        hasSpecialChar = hasSpecialChar,
                        strings = strings
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
                        Text(strings.submit, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = strings.alreadyHaveAccount,
                        color = WaypusTextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = strings.login,
                        color = ObcampusPrimary,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNavigateBackToLogin() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordRequirementsChecklist(
    hasMinLength: Boolean,
    hasUppercase: Boolean,
    hasDigit: Boolean,
    hasSpecialChar: Boolean,
    strings: SignUpStrings
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RequirementRow(strings.reqMinLength, hasMinLength)
        RequirementRow(strings.reqUppercase, hasUppercase)
        RequirementRow(strings.reqDigit, hasDigit)
        RequirementRow(strings.reqSpecialChar, hasSpecialChar)
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