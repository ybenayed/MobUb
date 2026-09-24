package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.model.weather.AirQualityAtDto
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

private data class PollutantInfo(
    val id: String,
    val label: String,
    val icon: String,
    val unit: String,
    val rawExplanation: String
)

private val PM25_INFO = PollutantInfo(
    id = "PM25",
    label = "PM2.5",
    icon = "\uD83C\uDF2B\uFE0F",
    unit = "µg/m³",
    rawExplanation = "Particules très fines en suspension dans l'air, assez petites pour pénétrer profondément dans les poumons et le sang. Elles proviennent surtout de la circulation, du chauffage et de l'industrie."
)

private val PM10_INFO = PollutantInfo(
    id = "PM10",
    label = "PM10",
    icon = "\uD83D\uDCA8",
    unit = "µg/m³",
    rawExplanation = "Particules en suspension plus grosses que les PM2.5. Elles irritent surtout les voies respiratoires supérieures (nez, gorge)."
)

private val OZONE_INFO = PollutantInfo(
    id = "OZONE",
    label = "Ozone (O3)",
    icon = "\u2600\uFE0F",
    unit = "µg/m³",
    rawExplanation = "Gaz formé près du sol sous l'effet du soleil, à partir d'autres polluants. Sa concentration est souvent plus élevée en été et l'après-midi, et il est irritant pour les voies respiratoires."
)

private val NO2_INFO = PollutantInfo(
    id = "NO2",
    label = "NO2",
    icon = "\uD83D\uDE97",
    unit = "µg/m³",
    rawExplanation = "Dioxyde d'azote, émis principalement par le trafic routier et la combustion. Il peut aggraver l'asthme et les problèmes respiratoires."
)

@Composable
fun AirQualityPanel(
    dailyData: AirQualityAtDto?,
    hourData: AirQualityAtDto?,
    showHourly: Boolean,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val data = if (showHourly) hourData else dailyData
    var infoDialog by remember { mutableStateOf<PollutantInfo?>(null) }
    var showCategoryInfo by remember { mutableStateOf(false) }

    // États de traduction pour l'interface statique
    var textHeaderHourly by remember { mutableStateOf("Qualité de l'air — à cette heure") }
    var textHeaderDaily by remember { mutableStateOf("Qualité de l'air — moyenne du jour") }
    var textUnavailable by remember { mutableStateOf("Donnée indisponible") }
    var textDialogTitle by remember { mutableStateOf("Indice de qualité de l'air") }
    var textDialogBody by remember { mutableStateOf("") }

    // États traduits dynamiquement pour le dialogue de chaque polluant
    var translatedExplanation by remember { mutableStateOf("") }

    LaunchedEffect(currentLanguage) {
        textHeaderHourly = languageViewModel.translate("Qualité de l'air — à cette heure")
        textHeaderDaily = languageViewModel.translate("Qualité de l'air — moyenne du jour")
        textUnavailable = languageViewModel.translate("Donnée indisponible")
        textDialogTitle = languageViewModel.translate("Indice de qualité de l'air")
        textDialogBody = languageViewModel.translate(
            "Cet indice résume le niveau global de pollution de l'air à partir de " +
                    "plusieurs polluants (PM2.5, PM10, ozone, NO2). Plus la catégorie est " +
                    "élevée, plus il est recommandé de limiter les efforts physiques " +
                    "prolongés en extérieur."
        )
    }

    LaunchedEffect(currentLanguage, infoDialog) {
        infoDialog?.let {
            translatedExplanation = languageViewModel.translate(it.rawExplanation)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (showHourly) textHeaderHourly else textHeaderDaily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (data == null) {
                Text(text = textUnavailable, fontSize = 13.sp, color = Color.Gray)
            } else {
                var translatedCategory by remember { mutableStateOf(data.category ?: "") }
                var translatedDesc by remember { mutableStateOf(data.description ?: "") }

                LaunchedEffect(currentLanguage, data.category, data.description) {
                    translatedCategory = data.category?.let { languageViewModel.translate(it) } ?: ""
                    translatedDesc = data.description?.let { languageViewModel.translate(it) } ?: ""
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showCategoryInfo = true }
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = data.icon ?: "🌍", fontSize = 30.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = translatedCategory, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Text(text = translatedDesc, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(4.dp))

                val pm25 = if (showHourly) data.pm2_5 else data.pm2_5Avg
                val pm10 = if (showHourly) data.pm10 else data.pm10Avg
                val ozone = if (showHourly) data.ozone else data.ozoneAvg
                val no2 = if (showHourly) data.nitrogenDioxide else data.nitrogenDioxideAvg

                PollutantRow(PM25_INFO, pm25) { infoDialog = PM25_INFO }
                Divider()
                PollutantRow(PM10_INFO, pm10) { infoDialog = PM10_INFO }
                Divider()
                PollutantRow(OZONE_INFO, ozone) { infoDialog = OZONE_INFO }
                Divider()
                PollutantRow(NO2_INFO, no2) { infoDialog = NO2_INFO }
            }
        }
    }

    infoDialog?.let { info ->
        AlertDialog(
            onDismissRequest = { infoDialog = null },
            confirmButton = { TextButton(onClick = { infoDialog = null }) { Text("OK") } },
            title = { Text("${info.icon}  ${info.label}") },
            text = { Text(translatedExplanation) }
        )
    }

    if (showCategoryInfo) {
        AlertDialog(
            onDismissRequest = { showCategoryInfo = false },
            confirmButton = { TextButton(onClick = { showCategoryInfo = false }) { Text("OK") } },
            title = { Text(textDialogTitle) },
            text = { Text(textDialogBody) }
        )
    }
}

@Composable
private fun PollutantRow(info: PollutantInfo, value: Double?, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(text = info.icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = info.label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value?.let { String.format("%.1f %s", it, info.unit) } ?: "--",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}