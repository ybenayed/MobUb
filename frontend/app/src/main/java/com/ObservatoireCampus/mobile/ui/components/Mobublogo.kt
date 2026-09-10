package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusTextWhite
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted

/**
 * Logo de l'application "MobUB" : icone dans un badge arrondi + wordmark bicolore.
 * "Mob" en texte sombre, "UB" dans la couleur de marque (ObcampusPrimary).
 *
 * Remplace WaypusLogo. Utilisable en grand sur l'ecran de login, et en petit
 * (badgeSize reduit, showTagline = false) dans la barre du haut une fois connecte.
 *
 * @param showTagline affiche ou non la petite phrase sous le logo.
 * @param badgeSize taille de l'icone ronde (64dp sur le login, ~32-36dp en top bar).
 * @param wordmarkFontSize taille du texte "MobUB".
 */
@Composable
fun MobUbLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = true,
    tagline: String = "Mobilite intelligente du campus de Bordeaux",
    badgeSize: Dp = 64.dp,
    wordmarkFontSize: androidx.compose.ui.unit.TextUnit = 26.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(badgeSize)
                .clip(RoundedCornerShape(badgeSize.value.dp * 0.28f))
                .background(ObcampusPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = ObcampusTextWhite,
                modifier = Modifier.size(badgeSize * 0.5f)
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = WaypusTextDark, fontWeight = FontWeight.Bold)) {
                    append("Mob")
                }
                withStyle(style = SpanStyle(color = ObcampusPrimary, fontWeight = FontWeight.Bold)) {
                    append("UB")
                }
            },
            fontSize = wordmarkFontSize,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (showTagline) {
            Text(
                text = tagline,
                style = MaterialTheme.typography.bodySmall,
                color = WaypusTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, start = 24.dp, end = 24.dp)
            )
        }
    }
}

/**
 * Version compacte du logo, pensee pour une TopAppBar (a gauche).
 * @param onColoredBackground true quand le fond est vert (ObcampusPrimary) :
 *   bascule le texte et le badge en blanc pour rester lisible, au lieu des
 *   couleurs "Mob" fonce / "UB" vert qui se fondraient dans le fond.
 */
@Composable
fun MobUbTopBarBrand(
    modifier: Modifier = Modifier,
    onColoredBackground: Boolean = false
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(if (onColoredBackground) ObcampusTextWhite else ObcampusPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (onColoredBackground) ObcampusPrimary else ObcampusTextWhite,
                modifier = Modifier.size(18.dp)
            )
        }
        if (onColoredBackground) {
            Text(
                text = "MobUB",
                color = ObcampusTextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = WaypusTextDark, fontWeight = FontWeight.Bold)) {
                        append("Mob")
                    }
                    withStyle(style = SpanStyle(color = ObcampusPrimary, fontWeight = FontWeight.Bold)) {
                        append("UB")
                    }
                },
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}