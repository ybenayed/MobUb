package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.admin.AdminStationVDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationVRequestDto
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.admin.AdminInfrastructureRepository
import com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminStationVViewModel
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminStationVViewModelFactory

@Composable
fun AdminStationVTab(
    strings: InfraStrings,
    viewModel: AdminStationVViewModel = viewModel(
        factory = AdminStationVViewModelFactory(AdminInfrastructureRepository(RetrofitClient.adminInfrastructureApi))
    )
) {
    val stations by viewModel.stations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var stationBeingEdited by remember { mutableStateOf<AdminStationVDto?>(null) }
    var stationPendingDeletion by remember { mutableStateOf<AdminStationVDto?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Barre de recherche
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text(strings.searchHint) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ObcampusPrimary,
                    unfocusedBorderColor = Color.LightGray
                )
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

                    error != null && stations.isEmpty() -> Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )

                    stations.isEmpty() -> Text(
                        text = strings.empty,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(stations, key = { it.id }) { station ->
                            StationVCard(
                                station = station,
                                strings = strings,
                                isDeleting = station.id in deletingIds,
                                onEditClick = {
                                    stationBeingEdited = station
                                    showForm = true
                                },
                                onDeleteClick = { stationPendingDeletion = station }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                stationBeingEdited = null
                showForm = true
            },
            containerColor = ObcampusPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = strings.addTitle, tint = Color.White)
        }
    }

    if (showForm) {
        val nextId = remember(stations) {
            val maxExisting = stations.mapNotNull { it.stationId.toIntOrNull() }.maxOrNull() ?: 0
            (maxExisting + 1).toString()
        }
        StationVFormSheet(
            strings = strings,
            existing = stationBeingEdited,
            nextAutoStationId = nextId,
            isSaving = isSaving,
            onDismiss = { showForm = false },
            onSubmit = { request ->
                viewModel.save(stationBeingEdited?.id, request) {
                    showForm = false
                }
            }
        )
    }

    stationPendingDeletion?.let { station ->
        AlertDialog(
            onDismissRequest = { stationPendingDeletion = null },
            title = { Text("${strings.confirmDeleteTitle} ${station.nom ?: station.stationId} ?") },
            text = { Text(strings.confirmDeleteMessage) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(station.id)
                    stationPendingDeletion = null
                }) {
                    Text(strings.confirmDelete, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { stationPendingDeletion = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun StationVCard(
    station: AdminStationVDto,
    strings: InfraStrings,
    isDeleting: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = null,
                tint = ObcampusPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(station.nom ?: station.stationId, fontWeight = FontWeight.SemiBold)
                station.adresse?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                station.capacite?.let {
                    Text("$it ${strings.capacitePlaces}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            if (isDeleting) {
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }
            } else {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Éditer", tint = ObcampusPrimary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationVFormSheet(
    strings: InfraStrings,
    existing: AdminStationVDto?,
    nextAutoStationId: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (AdminStationVRequestDto) -> Unit
) {
    val stationId = existing?.stationId ?: nextAutoStationId
    var nom by remember { mutableStateOf(existing?.nom ?: "") }
    var adresse by remember { mutableStateOf(existing?.adresse ?: "") }
    var capacite by remember { mutableStateOf(existing?.capacite?.toString() ?: "") }
    var latitude by remember { mutableStateOf(existing?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(existing?.longitude?.toString() ?: "") }

    val isEditMode = existing != null
    val canSubmit = nom.isNotBlank()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEditMode) strings.editTitle else strings.addTitle,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = ObcampusPrimary
            )

            Text(
                text = "${strings.fieldStationId} : $stationId",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text(strings.fieldNom) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = adresse,
                onValueChange = { adresse = it },
                label = { Text(strings.fieldAdresse) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = capacite,
                onValueChange = { capacite = it.filter { c -> c.isDigit() } },
                label = { Text(strings.fieldCapacite) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text(strings.fieldLatitude) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text(strings.fieldLongitude) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(strings.cancel)
                }
                Button(
                    onClick = {
                        onSubmit(
                            AdminStationVRequestDto(
                                stationId = stationId,
                                nom = nom.ifBlank { null },
                                adresse = adresse.ifBlank { null },
                                capacite = capacite.toIntOrNull(),
                                latitude = latitude.toDoubleOrNull(),
                                longitude = longitude.toDoubleOrNull()
                            )
                        )
                    },
                    enabled = canSubmit && !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = ObcampusPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(strings.save)
                    }
                }
            }
        }
    }
}