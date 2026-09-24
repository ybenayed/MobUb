package com.ObservatoireCampus.mobile.ui.components.parking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.parking.ParkingStatusDto
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingTypeStyle
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.ui.components.BubbleErrorContent

@Composable
fun ParkingBubble(
    status: ParkingStatusDto?,
    loading: Boolean,
    onClose: () -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    // 1. Déclaration de tous les états pour stocker les textes traduits
    var textEtatLabel by remember { mutableStateOf("État") }
    var textEtatValue by remember { mutableStateOf("Non renseigné") }
    var textPlacesLibres by remember { mutableStateOf("Places libres") }
    var textTypeLabel by remember { mutableStateOf("Type") }
    var textGestionnaire by remember { mutableStateOf("Gestionnaire") }
    var textAdresse by remember { mutableStateOf("Adresse") }
    var textHauteurMax by remember { mutableStateOf("Hauteur max") }
    var textTarifs by remember { mutableStateOf("Tarifs") }
    var textFermer by remember { mutableStateOf("Fermer") }
    var textIndisponible by remember { mutableStateOf("Informations indisponibles") }
    var textDonneesObsolescentes by remember { mutableStateOf("Données dynamiques obsolètes") }
    var textNonRenseigne by remember { mutableStateOf("Non renseigné") }

    // États spécifiques aux lignes de tarifs
    var textHeure1 by remember { mutableStateOf("1 heure") }
    var textHeures2 by remember { mutableStateOf("2 heures") }
    var textHeures3 by remember { mutableStateOf("3 heures") }
    var textHeures4 by remember { mutableStateOf("4 heures") }
    var textHeures24 by remember { mutableStateOf("24 heures") }
    var textNuit by remember { mutableStateOf("Nuit") }

    var translatedStructureLabel by remember { mutableStateOf("Parking") }
    var translatedTypeLabel by remember { mutableStateOf("Non renseigné") }

    // 2. Gestion de toutes les traductions suspendues au même endroit
    LaunchedEffect(status, currentLanguage) {
        // Traductions des libellés fixes (déclenchées au changement de langue)
        textEtatLabel = languageViewModel.translate("État")
        textPlacesLibres = languageViewModel.translate("Places libres")
        textTypeLabel = languageViewModel.translate("Type")
        textGestionnaire = languageViewModel.translate("Gestionnaire")
        textAdresse = languageViewModel.translate("Adresse")
        textHauteurMax = languageViewModel.translate("Hauteur max")
        textTarifs = languageViewModel.translate("Tarifs")
        textFermer = languageViewModel.translate("Fermer")
        textIndisponible = languageViewModel.translate("Informations indisponibles")
        textDonneesObsolescentes = languageViewModel.translate("Données dynamiques obsolètes")
        textNonRenseigne = languageViewModel.translate("Non renseigné")

        textHeure1 = languageViewModel.translate("1 heure")
        textHeures2 = languageViewModel.translate("2 heures")
        textHeures3 = languageViewModel.translate("3 heures")
        textHeures4 = languageViewModel.translate("4 heures")
        textHeures24 = languageViewModel.translate("24 heures")
        textNuit = languageViewModel.translate("Nuit")

        if (status != null) {
            translatedStructureLabel = ParkingTypeStyle.structureLabel(status.type, languageViewModel)
            translatedTypeLabel = status.taType?.let { ParkingTypeStyle.label(it, languageViewModel) } ?: "Non renseigné"

            // Traduction dynamique de la valeur de l'état du parking
            val etatRaw = when (status.etat) {
                "OUVERT" -> "Ouvert"
                "COMPLET" -> "Complet"
                "FERME" -> "Fermé"
                else -> "Non renseigné"
            }
            textEtatValue = languageViewModel.translate(etatRaw)
        }
    }

    Card(
        modifier = modifier.widthIn(max = 340.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {

            // EN-TÊTE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PARKING",
                        style = MaterialTheme.typography.labelSmall,
                        color = status?.taType?.let { ParkingTypeStyle.color(it) } ?: Color(0xFF616161),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ParkingTypeStyle.structureIcon(status?.type),
                            contentDescription = translatedStructureLabel,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = status?.nom ?: "Parking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = textFermer,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                }
                errorMessage != null -> {
                    BubbleErrorContent(
                        message = errorMessage,
                        onRetry = onRetry,
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage
                    )
                }
                status == null -> {
                    Text(
                        text = textIndisponible,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        // --- Gestion de l'état ---
                        val etatColor = when (status.etat) {
                            "OUVERT" -> Color(0xFF4CAF50)
                            "COMPLET" -> Color(0xFFD32F2F)
                            "FERME" -> Color(0xFF616161)
                            else -> Color(0xFF9E9E9E)
                        }
                        InfoRow(label = textEtatLabel, value = textEtatValue, valueColor = etatColor)

                        // --- Gestion des places libres ---
                        val placesValue = if (status.libre != null) {
                            if (status.npTotal != null) "${status.libre} / ${status.npTotal}" else "${status.libre}"
                        } else {
                            textNonRenseigne
                        }

                        val placesColor = when {
                            status.libre == null -> MaterialTheme.colorScheme.onSurface
                            status.npTotal != null && status.npTotal > 0 && (status.libre.toDouble() / status.npTotal < 0.1) -> Color(0xFFD32F2F)
                            else -> Color(0xFF1976D2)
                        }

                        InfoRow(label = textPlacesLibres, value = placesValue, valueColor = placesColor)

                        // --- Tarif / Type d'accès ---
                        InfoRow(
                            label = textTypeLabel,
                            value = if (translatedTypeLabel == "Non renseigné") textNonRenseigne else translatedTypeLabel
                        )

                        // --- Gestionnaire ---
                        InfoRow(label = textGestionnaire, value = status.exploit ?: textNonRenseigne)

                        // --- Adresse ---
                        InfoRow(label = textAdresse, value = status.adresse ?: textNonRenseigne, singleLineValue = false)

                        // --- Hauteur max (si dispo) ---
                        status.gabariMax?.let {
                            InfoRow(label = textHauteurMax, value = "${it}m")
                        }

                        // --- TARIFS HORAIRES (si parking payant) ---
                        val isPayant = status.taType?.uppercase()?.contains("PAYANT") == true
                        if (isPayant) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = textTarifs,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                TarifRow(label = "15 min", price = status.thQuar)
                                TarifRow(label = "30 min", price = status.thDemi)
                                TarifRow(label = textHeure1, price = status.thHeur)
                                TarifRow(label = textHeures2, price = status.th2)
                                TarifRow(label = textHeures3, price = status.th3)
                                TarifRow(label = textHeures4, price = status.th4)
                                TarifRow(label = textHeures24, price = status.th24)
                                TarifRow(label = textNuit, price = status.thNuit)
                            }
                        }

                        // --- ALERTE SI ALIMENTATION OBSOLÈTE ---
                        if (!status.dataFraiche) {
                            Text(
                                text = " $textDonneesObsolescentes",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    singleLineValue: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (value == "Non renseigné") FontWeight.Normal else FontWeight.Bold,
            color = if (value == "Non renseigné") Color.Gray else valueColor,
            maxLines = if (singleLineValue) 1 else 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f).padding(start = 8.dp)
        )
    }
}

@Composable
private fun TarifRow(
    label: String,
    price: Any?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val priceText = when (price) {
            null -> "-"
            is Number -> String.format(java.util.Locale.FRANCE, "%.2f €", price.toDouble())
            else -> price.toString()
        }

        Text(
            text = priceText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (priceText == "-") FontWeight.Normal else FontWeight.Bold,
            color = if (priceText == "-") Color.Gray else MaterialTheme.colorScheme.onSurface
        )
    }
}