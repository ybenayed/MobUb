package com.ObservatoireCampus.mobile.ui.components.navigation

/**
 * Routes de l'application. Fichier dedie (separe des ecrans) pour garder
 * la navigation modulaire, comme demande.[cite: 2]
 */
sealed class Screen(val route: String) {
    // AJOUT : ecrans d'authentification
    object Login : Screen("login")
    object SignUp : Screen("signup")

    object Map : Screen("map")

    // Route parametree : lat/lon optionnels (position utilisateur transmise depuis MapScreen).[cite: 2]
    // Pattern route brut pour le NavHost -> declaration des placeholders {lat} et {lon}.[cite: 2]
    object Weather : Screen("weather?lat={lat}&lon={lon}") {
        // Construit la route reelle a naviguer, avec les valeurs (ou vide si absentes).[cite: 2]
        fun buildRoute(lat: Double?, lon: Double?): String {
            val latParam = lat?.toString() ?: ""
            val lonParam = lon?.toString() ?: ""
            return "weather?lat=$latParam&lon=$lonParam"
        }
    }

    // AJOUT : Route statique pour l'écran "À propos" (Cadre de stage)
    object Internship : Screen("internship")
}