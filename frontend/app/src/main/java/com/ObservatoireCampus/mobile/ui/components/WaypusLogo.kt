package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusTextWhite
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted

/**
 * Logo de l'application WayPus : icone dans un badge arrondi + wordmark bicolore.
 * "Way" en texte sombre, "Pus" dans la couleur de marque (ObcampusPrimary).
 *
 * @param showTagline affiche ou non la petite phrase sous le logo (utile sur
 *   le login, superflu si on reutilise le logo ailleurs en plus petit).
 */
@Composable
fun WaypusLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(ObcampusPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = ObcampusTextWhite,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = WaypusTextDark, fontWeight = FontWeight.Bold)) {
                    append("Way")
                }
                withStyle(style = SpanStyle(color = ObcampusPrimary, fontWeight = FontWeight.Bold)) {
                    append("Pus")
                }
            },
            fontSize = 26.sp,
            modifier = Modifier.padding(top = 10.dp)
        )

        if (showTagline) {
            Text(
                text = "Mobilite intelligente du campus de Bordeaux",
                style = MaterialTheme.typography.bodySmall,
                color = WaypusTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, start = 24.dp, end = 24.dp)
            )
        }
    }
}