package com.ObservatoireCampus.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // <-- Ajoutez cette dépendance ktx
import androidx.compose.material3.MaterialTheme
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.ui.components.navigation.AppNavHost
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

class MainActivity : ComponentActivity() {

    // On instancie le ViewModel lié à l'Activity (unique pour toute l'appli)
    private val languageViewModel: LanguageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AJOUT : indispensable avant tout appel reseau (login/register/getCurrentUser).
        // Sans cette ligne, RetrofitClient.authApi (et tous les autres Api) plantent
        // avec "lateinit property tokenManager has not been initialized".
        RetrofitClient.init(this)

        setContent {
            MaterialTheme {
                // On passe l'instance unique au NavHost
                AppNavHost(languageViewModel = languageViewModel)
            }
        }
    }
}