package com.ObservatoireCampus.mobile.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.admin.AdminStatsTab
import com.ObservatoireCampus.mobile.ui.components.admin.AdminUsersTab
import com.ObservatoireCampus.mobile.ui.components.admin.AdminUsersStrings
import com.ObservatoireCampus.mobile.ui.components.admin.translateAdminUsersStrings
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var strings by remember { mutableStateOf(AdminUsersStrings()) }
    LaunchedEffect(currentLanguage) {
        strings = languageViewModel.translateAdminUsersStrings()
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Utilisateurs", "Statistiques")

    Scaffold(
        topBar = {
            TopBar(languageViewModel = languageViewModel, onMenuClick = onBack, isBackButton = true)
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Text(
                text = strings.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = ObcampusPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            TabRow(selectedTabIndex = selectedTab, contentColor = ObcampusPrimary) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label) }
                    )
                }
            }

            when (selectedTab) {
                0 -> AdminUsersTab(strings = strings)
                1 -> AdminStatsTab()
            }
        }
    }
}