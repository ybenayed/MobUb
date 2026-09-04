package com.ObservatoireCampus.mobile.ui.components.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryTimeFilter(
    date: String?,
    time: String?,
    arriveBy: Boolean,
    onChange: (date: String?, time: String?, arriveBy: Boolean) -> Unit,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedDepartAt by remember { mutableStateOf("Partir à") }
    var translatedArriveAt by remember { mutableStateOf("Arriver à") }
    var translatedNow by remember { mutableStateOf("Maintenant") }
    var translatedOk by remember { mutableStateOf("OK") }

    LaunchedEffect(currentLanguage) {
        translatedDepartAt = languageViewModel.translate("Partir à")
        translatedArriveAt = languageViewModel.translate("Arriver à")
        translatedNow = languageViewModel.translate("Maintenant")
        translatedOk = languageViewModel.translate("OK")
    }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = true)

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = !arriveBy,
                onClick = { onChange(date, time, false) },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) { Text(translatedDepartAt) }
            SegmentedButton(
                selected = arriveBy,
                onClick = { onChange(date, time, true) },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) { Text(translatedArriveAt) }
        }

        AssistChip(
            onClick = { showTimePicker = true },
            label = { Text(time ?: translatedNow) }
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val h = timePickerState.hour.toString().padStart(2, '0')
                    val m = timePickerState.minute.toString().padStart(2, '0')
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    onChange(today, "$h:$m", arriveBy)
                    showTimePicker = false
                }) { Text(translatedOk) }
            },
            dismissButton = {
                TextButton(onClick = {
                    onChange(null, null, arriveBy)
                    showTimePicker = false
                }) { Text(translatedNow) }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}