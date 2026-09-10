package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.ObservatoireCampus.mobile.R
import com.ObservatoireCampus.mobile.ui.components.LanguageSelector
import com.ObservatoireCampus.mobile.ui.components.MobUbLogo
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.WaypusAuthBackground
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

private data class LoginStrings(
    val tagline: String = "Mobilite intelligente du campus de Bordeaux",
    val busTram: String = "Bus / tram",
    val velos: String = "Velos",
    val parkings: String = "Parkings",
    val username: String = "Nom d'utilisateur",
    val email: String = "Email",
    val password: String = "Mot de passe",
    val showPassword: String = "Afficher le mot de passe",
    val hidePassword: String = "Masquer le mot de passe",
    val login: String = "Se connecter",
    val noAccountYet: String = "Pas encore de compte ? ",
    val createAccount: String = "Creer un compte",
    val projectBy: String = "Un projet de"
)

private suspend fun LanguageViewModel.translateLoginStrings(): LoginStrings {
    val base = LoginStrings()
    return LoginStrings(
        tagline = translate(base.tagline),
        busTram = translate(base.busTram),
        velos = translate(base.velos),
        parkings = translate(base.parkings),
        username = translate(base.username),
        email = translate(base.email),
        password = translate(base.password),
        showPassword = translate(base.showPassword),
        hidePassword = translate(base.hidePassword),
        login = translate(base.login),
        noAccountYet = translate(base.noAccountYet),
        createAccount = translate(base.createAccount),
        projectBy = translate(base.projectBy)
    )
}

@Composable
fun LoginScreen(
    languageViewModel: LanguageViewModel,
    onLoginClick: (username: String, email: String, password: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var strings by remember { mutableStateOf(LoginStrings()) }
    LaunchedEffect(currentLanguage) {
        strings = languageViewModel.translateLoginStrings()
    }

    val defaultCenter = remember { GeoPoint(44.8069, -0.5959) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    DisposableEffect(Unit) {
        onDispose { mapViewRef?.onDetach() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Fond Carte OSM
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                Configuration.getInstance().userAgentValue = ctx.packageName

                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(false)
                    zoomController.setVisibility(
                        org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
                    )
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

        // Voile sombre
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.10f))
        )

        // Sélecteur de langue
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPaddingSafe()
                .padding(top = 16.dp, end = 16.dp)
                .zIndex(100f)
        ) {
            LanguageSelector(languageViewModel = languageViewModel)
        }

        // Formulaire
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(WaypusAuthBackground.copy(alpha = 0.55f))
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                MobUbLogo(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    showTagline = true,
                    tagline = strings.tagline
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MobilityBadge(icon = Icons.Default.DirectionsBus, label = strings.busTram)
                    MobilityBadge(icon = Icons.Default.DirectionsBike, label = strings.velos)
                    MobilityBadge(icon = Icons.Default.LocalParking, label = strings.parkings)
                }

                Spacer(modifier = Modifier.height(28.dp))

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

                Text(
                    text = "Mot de passe oublié ?",
                    color = ObcampusPrimary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .clickable { onNavigateToForgotPassword() }
                )

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
                    onClick = { onLoginClick(username, email, password) },
                    enabled = !isLoading,
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
                        Text(strings.login, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = strings.noAccountYet,
                        color = WaypusTextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = strings.createAccount,
                        color = ObcampusPrimary,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNavigateToSignUp() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_chaire_mobilite),
                        contentDescription = "Logo Chaire Mobilite",
                        modifier = Modifier.height(28.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo_bordeaux_inp),
                        contentDescription = "Logo Bordeaux INP",
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Modifier.statusBarsPaddingSafe(): Modifier = this.padding(top = 8.dp)

@Composable
private fun MobilityBadge(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ObcampusPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = WaypusTextMuted)
    }
}

@Composable
private fun waypusFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ObcampusPrimary,
    unfocusedBorderColor = WaypusTextMuted,
    focusedLabelColor = ObcampusPrimary,
    unfocusedLabelColor = WaypusTextDark,
    cursorColor = ObcampusPrimary,
    focusedTextColor = WaypusTextDark,
    unfocusedTextColor = WaypusTextDark
)