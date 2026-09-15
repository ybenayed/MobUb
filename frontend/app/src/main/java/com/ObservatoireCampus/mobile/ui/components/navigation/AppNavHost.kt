package com.ObservatoireCampus.mobile.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.AuthRepository
import com.ObservatoireCampus.mobile.ui.screens.AccountScreen
import com.ObservatoireCampus.mobile.ui.screens.ForgotPasswordScreen
import com.ObservatoireCampus.mobile.ui.screens.InternshipScreen
import com.ObservatoireCampus.mobile.ui.screens.LoginScreen
import com.ObservatoireCampus.mobile.ui.screens.MapScreen
import com.ObservatoireCampus.mobile.ui.screens.SearchHistoryScreen
import com.ObservatoireCampus.mobile.ui.screens.SignUpScreen
import com.ObservatoireCampus.mobile.ui.screens.WeatherScreen
import com.ObservatoireCampus.mobile.viewmodel.AccountViewModel
import com.ObservatoireCampus.mobile.viewmodel.AccountViewModelFactory
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

    val authRepository = remember {
        AuthRepository(
            authApi = RetrofitClient.authApi,
            tokenManager = RetrofitClient.getTokenManager()
        )
    }

    // AJOUT : porte l'itineraire choisi depuis l'ecran Historique jusqu'a MapScreen.
    // Reste valide tant qu'AppNavHost n'est pas recompose depuis zero (i.e. tant que
    // l'app tourne), contrairement a un ViewModel scope a une destination du NavHost.
    var pendingHistoryItem by remember { mutableStateOf<SearchHistoryDto?>(null) }

    // CORRECTIF : si un token existe deja (session precedente non deconnectee),
    // on demarre directement sur la carte au lieu de repasser par Login a chaque
    // fois qu'Android recree le process (changement d'appli, mise en arriere-plan,
    // rotation memoire...). Sans ca, l'utilisateur avait l'impression d'etre
    // deconnecte alors que son token etait toujours valide et stocke.
    val startDestination = remember {
        if (RetrofitClient.getTokenManager().hasToken()) {
            Screen.Map.route
        } else {
            Screen.Login.route
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
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

            LoginScreen(
                languageViewModel = languageViewModel,
                onLoginClick = { username, email, password ->
                    authViewModel.login(username, email, password)
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
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

        composable(Screen.Map.route) {
            MapScreen(
                languageViewModel = languageViewModel,
                onWeatherClick = { lat, lon ->
                    navController.navigate(Screen.Weather.buildRoute(lat, lon))
                },
                onInternshipClick = {
                    navController.navigate(Screen.Internship.route)
                },
                onAccountClick = {
                    navController.navigate(Screen.Account.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                },
                historyItemToShow = pendingHistoryItem,
                onHistoryItemShown = { pendingHistoryItem = null },
                onLogout = {
                    // Seul endroit ou le token doit etre efface : demande EXPLICITE
                    // de l'utilisateur via le menu (bouton Deconnexion).
                    RetrofitClient.getTokenManager().clearToken()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.History.route) {
            SearchHistoryScreen(
                languageViewModel = languageViewModel, // AJOUT : necessaire pour TopBar + traductions
                onBack = { navController.popBackStack() },
                onViewOnMap = { item ->
                    pendingHistoryItem = item
                    navController.popBackStack() // revient sur Map, qui est deja dans la pile
                }
            )
        }

        composable(Screen.Account.route) {
            val accountViewModel: AccountViewModel = viewModel(
                factory = AccountViewModelFactory(authRepository, languageViewModel)
            )
            AccountScreen(
                accountViewModel = accountViewModel,
                languageViewModel = languageViewModel,
                onBack = { navController.popBackStack() }
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

        composable("forgot_password") {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository, languageViewModel)
            )
            val uiState by authViewModel.uiState.collectAsState()

            ForgotPasswordScreen(
                languageViewModel = languageViewModel,
                onSubmitEmail = { email ->
                    authViewModel.requestPasswordReset(email)
                },
                onConfirmReset = { request ->
                    authViewModel.confirmPasswordReset(request)
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                },
                isLoading = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message,
                isCodeSent = uiState is AuthUiState.ResetPasswordCodeSent,
                isCompleted = uiState is AuthUiState.ResetPasswordCompleted
            )
        }
    }
}