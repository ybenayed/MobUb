package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.history.SearchHistoryCard
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.history.SearchHistoryViewModel

/** Libelles traduits, meme pattern que AccountScreen. */
private data class HistoryStrings(
    val title: String = "Historique",
    val loading: String = "Chargement de l'historique...",
    val empty: String = "Aucun itinéraire enregistré pour le moment.",
    val delete: String = "Supprimer",
    val myLocation: String = "Ma position"   // <-- ajouté
)

private suspend fun LanguageViewModel.translateHistoryStrings(): HistoryStrings {
    val base = HistoryStrings()
    return HistoryStrings(
        title = translate(base.title),
        loading = translate(base.loading),
        empty = translate(base.empty),
        delete = translate(base.delete)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistoryScreen(
    languageViewModel: LanguageViewModel,          // <-- ajouté (nécessaire pour la trad + TopBar)
    onBack: () -> Unit,
    onViewOnMap: (SearchHistoryDto) -> Unit,
    viewModel: SearchHistoryViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var strings by remember { mutableStateOf(HistoryStrings()) }
    LaunchedEffect(currentLanguage) {
        strings = languageViewModel.translateHistoryStrings()
    }

    Scaffold(
        topBar = {
            TopBar(
                languageViewModel = languageViewModel,
                onMenuClick = onBack,
                isBackButton = true          // flèche retour, comme Mon compte
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Titre "Historique" sous la TopBar, meme style que "Mon compte"
            Text(
                text = strings.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = ObcampusPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(strings.loading)
                    }

                    error != null -> Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )

                    items.isEmpty() -> Text(
                        text = strings.empty,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items, key = { it.id }) { item ->
                            SearchHistoryCard(
                                item = item,
                                isDeleting = item.id in deletingIds,
                                myLocationLabel = strings.myLocation,   // <-- ajouté
                                deleteLabel = strings.delete,
                                onDelete = { viewModel.deleteItem(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}