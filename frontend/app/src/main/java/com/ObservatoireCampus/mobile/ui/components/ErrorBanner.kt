package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/**
 * Bandeau d'erreur.
 * - Le message s'affiche IMMEDIATEMENT (en francais), puis est remplace par sa traduction.
 * - Il reste affiche jusqu'a la croix, ou jusqu'a ce que l'erreur disparaisse.
 * - onRetry : si fourni, affiche un bouton "Reessayer".
 * - autoDismissMillis : par ex. 8_000 pour une fermeture automatique (null = jamais).
 */
@Composable
fun ErrorBanner(
    error: String?,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    autoDismissMillis: Long? = null
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var dismissed by remember(error) { mutableStateOf(false) }
    var translatedPrefix by remember(currentLanguage) { mutableStateOf("Erreur : ") }
    var translatedRetry by remember(currentLanguage) { mutableStateOf("Reessayer") }
    var translatedError by remember(error, currentLanguage) { mutableStateOf(error.orEmpty()) }

    LaunchedEffect(currentLanguage) {
        translatedPrefix = try {
            languageViewModel.translate("Erreur : ")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            "Erreur : "
        }
        translatedRetry = try {
            languageViewModel.translate("Reessayer")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            "Reessayer"
        }
    }

    LaunchedEffect(error, currentLanguage) {
        if (error != null) {
            translatedError = try {
                languageViewModel.translate(error)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error   // si la traduction echoue, on garde le texte d'origine
            }
        }
    }

    LaunchedEffect(error, autoDismissMillis) {
        if (error != null && autoDismissMillis != null) {
            delay(autoDismissMillis)
            dismissed = true
        }
    }

    if (error != null && !dismissed) {
        Surface(
            modifier = modifier,
            color = Color.Red.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$translatedPrefix${translatedError.ifBlank { error }}",
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                )
                if (onRetry != null) {
                    TextButton(
                        onClick = onRetry,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text(translatedRetry)
                    }
                }
                IconButton(
                    onClick = { dismissed = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = Color.White
                    )
                }
            }
        }
    }
}