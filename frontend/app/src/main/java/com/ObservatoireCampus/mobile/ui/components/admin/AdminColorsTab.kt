package com.ObservatoireCampus.mobile.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import com.ObservatoireCampus.mobile.network.RetrofitClient
import com.ObservatoireCampus.mobile.repository.admin.AdminLegendRepository
import com.ObservatoireCampus.mobile.ui.screens.admin.InfraStrings
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminColorsViewModel
import com.ObservatoireCampus.mobile.viewmodel.admin.AdminColorsViewModelFactory
import androidx.compose.ui.draw.clip
@Composable
fun AdminColorsTab(
    strings: InfraStrings,
    viewModel: AdminColorsViewModel = viewModel(
        factory = AdminColorsViewModelFactory(AdminLegendRepository(RetrofitClient.adminLegendApi))
    )
) {
    val colors by viewModel.colors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val deletingInstitutions by viewModel.deletingInstitutions.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var colorBeingEdited by remember { mutableStateOf<InstitutionColorDto?>(null) }
    var colorPendingDeletion by remember { mutableStateOf<InstitutionColorDto?>(null) }

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

            error != null && colors.isEmpty() -> Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )

            colors.isEmpty() -> Text(
                text = strings.empty,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(colors, key = { it.institution }) { colorDto ->
                    ColorCard(
                        colorDto = colorDto,
                        isDeleting = colorDto.institution in deletingInstitutions,
                        onEditClick = {
                            colorBeingEdited = colorDto
                            showForm = true
                        },
                        onDeleteClick = { colorPendingDeletion = colorDto }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                colorBeingEdited = null
                showForm = true
            },
            containerColor = ObcampusPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter", tint = Color.White)
        }
    }

    if (showForm) {
        ColorFormSheet(
            existing = colorBeingEdited,
            existingInstitutionNames = colors.map { it.institution },
            isSaving = isSaving,
            onDismiss = { showForm = false },
            onSubmit = { institution, color ->
                viewModel.save(institution, color) { showForm = false }
            }
        )
    }

    colorPendingDeletion?.let { colorDto ->
        AlertDialog(
            onDismissRequest = { colorPendingDeletion = null },
            title = { Text("Supprimer ${colorDto.institution} ?") },
            text = { Text("Cette action est irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(colorDto.institution)
                    colorPendingDeletion = null
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { colorPendingDeletion = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun ColorCard(
    colorDto: InstitutionColorDto,
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
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(parseColorSafe(colorDto.color))
                    .border(1.dp, Color.LightGray, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = colorDto.institution,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

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
private fun ColorFormSheet(
    existing: InstitutionColorDto?,
    existingInstitutionNames: List<String>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (institution: String, color: String) -> Unit
) {
    var institution by remember { mutableStateOf(existing?.institution ?: "") }
    var selectedColor by remember {
        mutableStateOf(existing?.color ?: INSTITUTION_COLOR_PALETTE.first())
    }

    val isEditMode = existing != null
    val nameCollision = !isEditMode && institution.isNotBlank() &&
            existingInstitutionNames.any { it.equals(institution, ignoreCase = true) }
    val canSubmit = institution.isNotBlank() && !nameCollision

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEditMode) "Modifier la couleur" else "Ajouter une institution",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = ObcampusPrimary
            )

            OutlinedTextField(
                value = institution,
                onValueChange = { institution = it },
                label = { Text("Nom de l'institution") },
                singleLine = true,
                enabled = !isEditMode, // le nom identifie l'entree, non modifiable en edition
                isError = nameCollision,
                supportingText = {
                    if (nameCollision) Text("Cette institution existe déjà")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Couleur", fontWeight = FontWeight.SemiBold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(INSTITUTION_COLOR_PALETTE) { hex ->
                    val isSelected = hex.equals(selectedColor, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(parseColorSafe(hex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) ObcampusPrimary else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Annuler")
                }
                Button(
                    onClick = { onSubmit(institution, selectedColor) },
                    enabled = canSubmit && !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = ObcampusPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

private fun parseColorSafe(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}
/** Palette fixe : l'admin choisit uniquement parmi ces couleurs, ppas de saisie libre. */
val INSTITUTION_COLOR_PALETTE = listOf(
    "#E53935", "#D81B60", "#8E24AA", "#5E35B1",
    "#3949AB", "#1E88E5", "#039BE5", "#00ACC1",
    "#00897B", "#43A047", "#7CB342", "#C0CA33",
    "#FDD835", "#FFB300", "#FB8C00", "#F4511E",
    "#6D4C41", "#757575", "#546E7A", "#EAF0D8"
)