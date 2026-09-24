package com.ObservatoireCampus.mobile.ui.components.Search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary

@Composable
fun AutocompleteField(
    value: String,
    suggestions: List<SearchResultDto>,
    onValueChange: (String) -> Unit,
    onSuggestionSelected: (SearchResultDto) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(imageVector = Icons.Default.Search, contentDescription = null)
    },
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 3.dp,
            color = Color.White
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ObcampusPrimary,
                    cursorColor = ObcampusPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                    itemsIndexed(suggestions) { index, suggestion ->
                        AutocompleteSuggestionRow(
                            suggestion = suggestion,
                            onClick = { onSuggestionSelected(suggestion) }
                        )
                        if (index < suggestions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 52.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFFECECEC)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AutocompleteSuggestionRow(
    suggestion: SearchResultDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
            contentDescription = null,
            tint = ObcampusPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (suggestion.subtitle.isNotBlank()) {
                Text(
                    text = suggestion.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}