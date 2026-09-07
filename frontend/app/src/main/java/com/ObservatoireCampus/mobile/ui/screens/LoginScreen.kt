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
import com.ObservatoireCampus.mobile.R
import com.ObservatoireCampus.mobile.ui.components.LanguageSelector
import com.ObservatoireCampus.mobile.ui.components.WaypusLogo
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.WaypusAuthBackground
import com.ObservatoireCampus.mobile.ui.theme.WaypusInputBorder
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

/**
 * Ecran de connexion. UI "bete" (pas d'appel reseau ici) : le username,
 * l'email et le mot de passe remontent via onLoginClick. Le branchement
 * avec le backend (AuthViewModel + Retrofit) se fait dans une prochaine etape.
 */
@Composable
fun LoginScreen(
    languageViewModel: LanguageViewModel,
    onLoginClick: (username: String, email: String, password: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WaypusAuthBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LanguageSelector(languageViewModel = languageViewModel)
        }

        Spacer(modifier = Modifier.height(8.dp))

        WaypusLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MobilityBadge(icon = Icons.Default.DirectionsBus, label = "Bus / tram")
            MobilityBadge(icon = Icons.Default.DirectionsBike, label = "Velos")
            MobilityBadge(icon = Icons.Default.LocalParking, label = "Parkings")
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                Text("Se connecter", fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pas encore de compte ? ",
                color = WaypusTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Creer un compte",
                color = ObcampusPrimary,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(color = WaypusInputBorder)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Un projet de",
            style = MaterialTheme.typography.labelSmall,
            color = WaypusTextMuted,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_chaire_mobilite),
                contentDescription = "Logo Chaire Mobilite",
                modifier = Modifier.height(32.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.logo_bordeaux_inp),
                contentDescription = "Logo Bordeaux INP",
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

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
    unfocusedBorderColor = WaypusInputBorder,
    cursorColor = ObcampusPrimary,
    focusedLabelColor = ObcampusPrimary,
    unfocusedLabelColor = WaypusTextMuted
)