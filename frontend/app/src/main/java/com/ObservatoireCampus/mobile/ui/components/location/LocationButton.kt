package com.ObservatoireCampus.mobile.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.location.LocationViewModel

@Composable
fun LocationButton(
    viewModel: LocationViewModel,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isActive by viewModel.isActive.collectAsState()

    var translatedContentDescription by remember { mutableStateOf("Me localiser") }

    LaunchedEffect(currentLanguage) {
        translatedContentDescription = languageViewModel.translate("Me localiser")
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchLocation()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    IconButton(
        onClick = {
            if (isActive) {
                viewModel.clearLocation()
                return@IconButton
            }
            if (hasLocationPermission()) {
                viewModel.fetchLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        modifier = modifier
            .size(44.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = translatedContentDescription,
            tint = if (isActive) Color(0xFF1976D2) else MaterialTheme.colorScheme.primary
        )
    }
}