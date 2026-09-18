package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.admin.AdminParkingDto
import com.ObservatoireCampus.mobile.model.admin.AdminParkingRequestDto
import com.ObservatoireCampus.mobile.model.admin.PARKING_STRUCTURE_TYPES
import com.ObservatoireCampus.mobile.model.admin.PARKING_TA_TYPES
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.admin.AdminInfrastructureRepository
import com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminParkingViewModel
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminParkingViewModelFactory
import androidx.compose.foundation.verticalScroll

@Composable
fun AdminParkingTab(
    strings: InfraStrings,
    viewModel: AdminParkingViewModel = viewModel(
        factory = AdminParkingViewModelFactory(AdminInfrastructureRepository(RetrofitClient.adminInfrastructureApi))
    )
) {
    val parkings by viewModel.parkings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var parkingBeingEdited by remember { mutableStateOf<AdminParkingDto?>(null) }
    var parkingPendingDeletion by remember { mutableStateOf<AdminParkingDto?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

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

                    error != null && parkings.isEmpty() -> Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )

                    parkings.isEmpty() -> Text(
                        text = strings.empty,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(parkings, key = { it.id }) { parking ->
                            ParkingCard(
                                parking = parking,
                                isDeleting = parking.id in deletingIds,
                                onEditClick = {
                                    parkingBeingEdited = parking
                                    showForm = true
                                },
                                onDeleteClick = { parkingPendingDeletion = parking }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                parkingBeingEdited = null
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
        val nextId = remember(parkings) { viewModel.nextAutoIdent() }
        ParkingFormSheet(
            strings = strings,
            existing = parkingBeingEdited,
            nextAutoIdent = nextId,
            isSaving = isSaving,
            onDismiss = { showForm = false },
            onSubmit = { request ->
                viewModel.save(parkingBeingEdited?.id, request) {
                    showForm = false
                }
            }
        )
    }

    parkingPendingDeletion?.let { parking ->
        AlertDialog(
            onDismissRequest = { parkingPendingDeletion = null },
            title = { Text("${strings.confirmDeleteTitle} ${parking.nom ?: parking.ident} ?") },
            text = { Text(strings.confirmDeleteMessage) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(parking.id)
                    parkingPendingDeletion = null
                }) {
                    Text(strings.confirmDelete, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { parkingPendingDeletion = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun ParkingCard(
    parking: AdminParkingDto,
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
            Icon(Icons.Default.LocalParking, contentDescription = null, tint = ObcampusPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(parking.nom ?: parking.ident, fontWeight = FontWeight.SemiBold)
                parking.taType?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                parking.npTotal?.let {
                    Text("$it places", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
private fun ParkingFormSheet(
    strings: InfraStrings,
    existing: AdminParkingDto?,
    nextAutoIdent: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (AdminParkingRequestDto) -> Unit
) {
    val ident = existing?.ident ?: nextAutoIdent
    var nom by remember { mutableStateOf(existing?.nom ?: "") }
    var adresse by remember { mutableStateOf(existing?.adresse ?: "") }
    var taType by remember { mutableStateOf(existing?.taType ?: PARKING_TA_TYPES.first()) }
    var structureType by remember { mutableStateOf(existing?.type ?: PARKING_STRUCTURE_TYPES.first()) }
    var npTotal by remember { mutableStateOf(existing?.npTotal?.toString() ?: "") }
    var latitude by remember { mutableStateOf(existing?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(existing?.longitude?.toString() ?: "") }
    var url by remember { mutableStateOf(existing?.url ?: "") }

    var thHeur by remember { mutableStateOf(existing?.thHeur?.toString() ?: "") }
    var thQuar by remember { mutableStateOf(existing?.thQuar?.toString() ?: "") }
    var thDemi by remember { mutableStateOf(existing?.thDemi?.toString() ?: "") }
    var th2 by remember { mutableStateOf(existing?.th2?.toString() ?: "") }
    var th3 by remember { mutableStateOf(existing?.th3?.toString() ?: "") }
    var th4 by remember { mutableStateOf(existing?.th4?.toString() ?: "") }
    var th10 by remember { mutableStateOf(existing?.th10?.toString() ?: "") }
    var th24 by remember { mutableStateOf(existing?.th24?.toString() ?: "") }
    var thNuit by remember { mutableStateOf(existing?.thNuit?.toString() ?: "") }
    var taTitul by remember { mutableStateOf(existing?.taTitul?.toString() ?: "") }
    var taNtitul by remember { mutableStateOf(existing?.taNtitul?.toString() ?: "") }
    var taResmoi by remember { mutableStateOf(existing?.taResmoi?.toString() ?: "") }
    var taNres7j by remember { mutableStateOf(existing?.taNres7j?.toString() ?: "") }

    val isEditMode = existing != null
    val canSubmit = nom.isNotBlank()
    val isPayant = taType != "GRATUIT"

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEditMode) strings.editTitle else strings.addTitle,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = ObcampusPrimary
            )

            Text(
                text = "${strings.fieldStationId} : $ident",
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

            DropdownField(
                label = "Type (carte)",
                options = PARKING_TA_TYPES,
                selected = taType,
                onSelected = { taType = it }
            )

            DropdownField(
                label = "Type de structure",
                options = PARKING_STRUCTURE_TYPES,
                selected = structureType,
                onSelected = { structureType = it }
            )

            OutlinedTextField(
                value = npTotal,
                onValueChange = { npTotal = it.filter { c -> c.isDigit() } },
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

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Section tarifs, visible seulement si parking payant ---
            if (isPayant) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "Tarifs",
                    fontWeight = FontWeight.SemiBold,
                    color = ObcampusPrimary
                )

                TarifRow("1/4h", thQuar, { thQuar = it }, "1/2h", thDemi, { thDemi = it })
                TarifRow("1h", thHeur, { thHeur = it }, "2h", th2, { th2 = it })
                TarifRow("3h", th3, { th3 = it }, "4h", th4, { th4 = it })
                TarifRow("10h", th10, { th10 = it }, "24h", th24, { th24 = it })
                TarifRow("Nuit", thNuit, { thNuit = it }, "Titulaire", taTitul, { taTitul = it })
                TarifRow("Non-titulaire", taNtitul, { taNtitul = it }, "Résa. mois", taResmoi, { taResmoi = it })

                OutlinedTextField(
                    value = taNres7j,
                    onValueChange = { taNres7j = it },
                    label = { Text("Résa. 7 jours") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(0.5f)
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
                            AdminParkingRequestDto(
                                ident = ident,
                                nom = nom.ifBlank { null },
                                adresse = adresse.ifBlank { null },
                                taType = taType,
                                type = structureType,
                                npTotal = npTotal.toIntOrNull(),
                                latitude = latitude.toDoubleOrNull(),
                                longitude = longitude.toDoubleOrNull(),
                                url = url.ifBlank { null },
                                thHeur = if (isPayant) thHeur.toDoubleOrNull() else null,
                                thQuar = if (isPayant) thQuar.toDoubleOrNull() else null,
                                thDemi = if (isPayant) thDemi.toDoubleOrNull() else null,
                                th2 = if (isPayant) th2.toDoubleOrNull() else null,
                                th3 = if (isPayant) th3.toDoubleOrNull() else null,
                                th4 = if (isPayant) th4.toDoubleOrNull() else null,
                                th10 = if (isPayant) th10.toDoubleOrNull() else null,
                                th24 = if (isPayant) th24.toDoubleOrNull() else null,
                                thNuit = if (isPayant) thNuit.toDoubleOrNull() else null,
                                taTitul = if (isPayant) taTitul.toDoubleOrNull() else null,
                                taNtitul = if (isPayant) taNtitul.toDoubleOrNull() else null,
                                taResmoi = if (isPayant) taResmoi.toDoubleOrNull() else null,
                                taNres7j = if (isPayant) taNres7j.toDoubleOrNull() else null
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

@Composable
private fun TarifRow(
    label1: String, value1: String, onChange1: (String) -> Unit,
    label2: String, value2: String, onChange2: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = value1,
            onValueChange = onChange1,
            label = { Text(label1) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value2,
            onValueChange = onChange2,
            label = { Text(label2) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}