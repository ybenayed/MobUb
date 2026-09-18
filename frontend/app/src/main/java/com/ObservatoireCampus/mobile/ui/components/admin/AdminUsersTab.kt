package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.auth.UserDto
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.admin.AdminUserRepository
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminUsersViewModel
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminUsersViewModelFactory

data class AdminUsersStrings(
    val title: String = "Gestion des utilisateurs",
    val loading: String = "Chargement des utilisateurs...",
    val empty: String = "Aucun utilisateur pour le moment.",
    val labelPhone: String = "Téléphone",
    val labelNationality: String = "Nationalité",
    val labelResidence: String = "Résidence",
    val labelCreatedAt: String = "Inscrit le",
    val notProvided: String = "Non renseigné",
    val detailTitle: String = "Fiche utilisateur",
    val close: String = "Fermer",
    val confirmTitle: String = "Supprimer",
    val confirmMessage: String = "Cette action est irréversible.",
    val confirmDelete: String = "Supprimer",
    val cancel: String = "Annuler"
)

suspend fun LanguageViewModel.translateAdminUsersStrings(): AdminUsersStrings {
    val base = AdminUsersStrings()
    return AdminUsersStrings(
        title = translate(base.title),
        loading = translate(base.loading),
        empty = translate(base.empty),
        labelPhone = translate(base.labelPhone),
        labelNationality = translate(base.labelNationality),
        labelResidence = translate(base.labelResidence),
        labelCreatedAt = translate(base.labelCreatedAt),
        notProvided = translate(base.notProvided),
        detailTitle = translate(base.detailTitle),
        close = translate(base.close),
        confirmTitle = translate(base.confirmTitle),
        confirmMessage = translate(base.confirmMessage),
        confirmDelete = translate(base.confirmDelete),
        cancel = translate(base.cancel)
    )
}

@Composable
fun AdminUsersTab(
    strings: AdminUsersStrings,
    viewModel: AdminUsersViewModel = viewModel(
        factory = AdminUsersViewModelFactory(AdminUserRepository(RetrofitClient.adminUserApi))
    )
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    var userPendingDeletion by remember { mutableStateOf<UserDto?>(null) }
    var userShownInDetail by remember { mutableStateOf<UserDto?>(null) }

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

            users.isEmpty() -> Text(
                text = strings.empty,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(users, key = { it.id }) { user ->
                    AdminUserCard(
                        user = user,
                        isDeleting = user.id in deletingIds,
                        onInfoClick = { userShownInDetail = user },
                        onDeleteClick = { userPendingDeletion = user }
                    )
                }
            }
        }
    }

    userShownInDetail?.let { user ->
        AlertDialog(
            onDismissRequest = { userShownInDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = ObcampusPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.detailTitle)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailRow(label = "Username", value = user.username)
                    DetailRow(label = "Email", value = user.email)
                    DetailRow(label = strings.labelPhone, value = user.phoneNumber ?: strings.notProvided)
                    DetailRow(label = strings.labelNationality, value = user.nationality ?: strings.notProvided)
                    DetailRow(label = strings.labelResidence, value = user.residence ?: strings.notProvided)
                    DetailRow(label = strings.labelCreatedAt, value = formatDate(user.createdAt))
                }
            },
            confirmButton = {
                TextButton(onClick = { userShownInDetail = null }) {
                    Text(strings.close)
                }
            }
        )
    }

    userPendingDeletion?.let { user ->
        AlertDialog(
            onDismissRequest = { userPendingDeletion = null },
            title = { Text("${strings.confirmTitle} ${user.username} ?") },
            text = { Text(strings.confirmMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(user.id)
                        userPendingDeletion = null
                    }
                ) {
                    Text(strings.confirmDelete, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { userPendingDeletion = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun AdminUserCard(
    user: UserDto,
    isDeleting: Boolean,
    onInfoClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ObcampusPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.take(1).uppercase(),
                    color = ObcampusPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isDeleting) {
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            } else {
                IconButton(onClick = onInfoClick) {
                    Icon(Icons.Default.Info, contentDescription = "Détail", tint = ObcampusPrimary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatDate(isoDateTime: String): String {
    return try {
        val datePart = isoDateTime.substringBefore("T")
        val (year, month, day) = datePart.split("-")
        "$day/$month/$year"
    } catch (e: Exception) {
        isoDateTime
    }
}