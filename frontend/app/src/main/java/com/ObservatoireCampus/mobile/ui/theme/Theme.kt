package com.ObservatoireCampus.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Sans ce fichier, MaterialTheme applique son schema de couleurs par defaut
 * (base sur Purple40/Purple80...), d'ou le mauve qui apparaissait un peu
 * partout (Switch, boutons par defaut, indicateurs, etc.). Ce theme
 * remplace ca par des couleurs coherentes avec le vert de la marque.
 *
 * Usage dans MainActivity :
 *   setContent {
 *       ObcampusTheme {
 *           AppNavHost(languageViewModel = languageViewModel)
 *       }
 *   }
 */
private val ObcampusLightColors = lightColorScheme(
    primary = ObcampusPrimary,
    onPrimary = ObcampusTextWhite,
    secondary = ObcampusSecondary,
    onSecondary = ObcampusTextWhite,
    tertiary = WaypusTextMuted,
    background = ObcampusBackground,
    onBackground = WaypusTextDark,
    surface = ObcampusTextWhite,
    onSurface = WaypusTextDark,
    surfaceVariant = WaypusAuthBackground,
    onSurfaceVariant = WaypusTextMuted
)

private val ObcampusDarkColors = darkColorScheme(
    primary = ObcampusSecondary,
    onPrimary = ObcampusTextWhite,
    secondary = ObcampusPrimary,
    onSecondary = ObcampusTextWhite,
    tertiary = WaypusTextMuted,
    background = WaypusTextDark,
    onBackground = ObcampusTextWhite,
    surface = WaypusTextDark,
    onSurface = ObcampusTextWhite
)

@Composable
fun ObcampusTheme(
    darkTheme: Boolean = false, // volontairement pas isSystemInDarkTheme() pour l'instant :
    // l'appli n'a pas encore de variante sombre testee visuellement.
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ObcampusDarkColors else ObcampusLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
    // NB : si vous avez deja un objet Typography (Type.kt genere par defaut par
    // Android Studio), dites-le moi et je l'ajoute ici : MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}