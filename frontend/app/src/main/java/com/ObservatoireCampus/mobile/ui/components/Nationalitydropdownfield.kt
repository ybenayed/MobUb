package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import java.text.Normalizer

private fun String.normalizeForSearch(): String =
    Normalizer.normalize(this.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalityDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    emptyText: String = "Chargement…",
    noResultText: String = "Aucun résultat"
) {
    var expanded by remember { mutableStateOf(false) }
    var isTyping by remember { mutableStateOf(false) }
    var fieldValue by remember { mutableStateOf(TextFieldValue(value)) }

    LaunchedEffect(value) {
        if (!isTyping) fieldValue = TextFieldValue(value)
    }

    val filteredOptions = remember(options, fieldValue.text, isTyping) {
        val q = fieldValue.text.normalizeForSearch()
        if (!isTyping || q.isEmpty()) options
        else options.filter { it.normalizeForSearch().startsWith(q) }
    }

    fun closeMenu() {
        expanded = false
        isTyping = false
        fieldValue = TextFieldValue(value)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { wantsExpanded ->
            if (enabled) {
                if (wantsExpanded) expanded = true else closeMenu()
            }
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = fieldValue,
            onValueChange = { new ->
                val old = fieldValue.text
                if (new.text == old) {
                    fieldValue = new
                } else {
                    val newText = if (isTyping) {
                        new.text
                    } else {
                        val inserted = new.text.length - old.length
                        val end = new.selection.start
                        if (inserted > 0 && end - inserted >= 0 && end <= new.text.length)
                            new.text.substring(end - inserted, end)
                        else if (new.selection.collapsed && old.isNotEmpty() && inserted <= 0)
                            ""
                        else
                            new.text
                    }
                    fieldValue = TextFieldValue(newText, TextRange(newText.length))
                    isTyping = true
                    expanded = true
                }
            },
            enabled = enabled,
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = colors,
            shape = shape,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { closeMenu() }
        ) {
            when {
                options.isEmpty() -> DropdownMenuItem(
                    text = { Text(emptyText) },
                    onClick = {},
                    enabled = false
                )
                filteredOptions.isEmpty() -> DropdownMenuItem(
                    text = { Text(noResultText) },
                    onClick = {},
                    enabled = false
                )
                else -> filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            closeMenu()
                        }
                    )
                }
            }
        }
    }
}