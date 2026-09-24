package com.ObservatoireCampus.mobile.ui.components.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")

    object Map : Screen("map")

    object Weather : Screen("weather?lat={lat}&lon={lon}") {
        fun buildRoute(lat: Double?, lon: Double?): String {
            val latParam = lat?.toString() ?: ""
            val lonParam = lon?.toString() ?: ""
            return "weather?lat=$latParam&lon=$lonParam"
        }
    }

    object Internship : Screen("internship")
    object Account : Screen("account")
    object History : Screen("search_history")

    object AdminUsers : Screen("admin_users")
    object AdminInfrastructure : Screen("admin_infrastructure")
    object AdminLegends : Screen("admin_legends")
}