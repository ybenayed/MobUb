package com.ObservatoireCampus.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.ui.components.navigation.AppNavHost
import com.ObservatoireCampus.mobile.ui.theme.ObcampusTheme
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

class MainActivity : ComponentActivity() {

    // ViewModel lie a l'Activity (unique pour toute l'appli)
    private val languageViewModel: LanguageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Indispensable avant tout appel reseau (login/register/getCurrentUser).
        RetrofitClient.init(this)

        setContent {
            // ObcampusTheme (voir Theme.kt) remplace MaterialTheme brut :
            // c'est ce qui elimine les couleurs violettes par defaut.
            ObcampusTheme {
                AppNavHost(languageViewModel = languageViewModel)
            }
        }
    }
}