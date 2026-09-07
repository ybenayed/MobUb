package com.ObservatoireCampus.mobile.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.AuthRepository
import com.ObservatoireCampus.mobile.ui.screens.LoginScreen
import com.ObservatoireCampus.mobile.ui.screens.SignUpScreen
import com.ObservatoireCampus.mobile.ui.screens.MapScreen
import com.ObservatoireCampus.mobile.ui.screens.WeatherScreen
import com.ObservatoireCampus.mobile.ui.screens.InternshipScreen
import com.ObservatoireCampus.mobile.viewmodel.AuthUiState
import com.ObservatoireCampus.mobile.viewmodel.AuthViewModel
import com.ObservatoireCampus.mobile.viewmodel.AuthViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun AppNavHost(
    languageViewModel: LanguageViewModel,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navController = rememberNavController()

    // Un seul AuthRepository partage entre Login et SignUp (evite de le recreer
    // a chaque navigation). S'appuie sur les singletons deja initialises dans
    // MainActivity via RetrofitClient.init(this).
    val authRepository = remember {
        AuthRepository(
            authApi = RetrofitClient.authApi,
            tokenManager = RetrofitClient.getTokenManager()
        )
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository, languageViewModel)
            )
            val uiState by authViewModel.uiState.collectAsState()

            // Reagit au resultat du login : navigue vers la carte en cas de succes.
            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    authViewModel.resetState()
                }
            }

            LoginScreen(
                languageViewModel = languageViewModel,
                onLoginClick = { username, email, password ->
                    authViewModel.login(username, email, password)
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                isLoading = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message
            )
        }

        composable(Screen.SignUp.route) {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository, languageViewModel)
            )
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    authViewModel.resetState()
                }
            }

            SignUpScreen(
                languageViewModel = languageViewModel,
                onSignUpClick = { formData ->
                    authViewModel.register(
                        com.ObservatoireCampus.mobile.model.auth.RegisterRequestDto(
                            username = formData.username,
                            email = formData.email,
                            phoneNumber = formData.phoneNumber,
                            nationality = formData.nationality,
                            residence = formData.residence,
                            password = formData.password
                        )
                    )
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                },
                isLoading = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message
            )
        }

        // MODIFIE : ajout de onLogout
        composable(Screen.Map.route) {
            MapScreen(
                languageViewModel = languageViewModel,
                onWeatherClick = { lat, lon ->
                    navController.navigate(Screen.Weather.buildRoute(lat, lon))
                },
                onInternshipClick = {
                    navController.navigate(Screen.Internship.route)
                },
                onLogout = {
                    RetrofitClient.getTokenManager().clearToken()
                    navController.navigate(Screen.Login.route) {
                        // Vide TOUT l'historique (Map, Weather, Internship...), pas
                        // seulement jusqu'a Login, car la deconnexion peut arriver
                        // depuis n'importe quel ecran protege.
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Weather.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("lon") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()

            WeatherScreen(
                languageViewModel = languageViewModel,
                onBack = { navController.popBackStack() },
                userLat = lat,
                userLon = lon
            )
        }

        composable(Screen.Internship.route) {
            InternshipScreen(
                languageViewModel = languageViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}