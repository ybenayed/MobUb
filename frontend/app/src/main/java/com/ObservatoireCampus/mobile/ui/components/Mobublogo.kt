package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
 * Pictogramme "pin de carte + trajet" du logo MobUB, dessine en Canvas
 * (proportions calquees sur le logo SVG valide).
 *
 * @param showRoute affiche le petit trajet en pointilles sous le pin
 *   (desactive dans les tres petits formats comme la TopBar, illisible sinon).
 */
@Composable
fun MobUbPinIcon(
    modifier: Modifier = Modifier,
    pinColor: Color = ObcampusTextWhite,
    holeColor: Color = ObcampusPrimary,
    routeColor: Color = ObcampusTextWhite,
    showRoute: Boolean = true
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val pinPath = Path().apply {
            moveTo(0.50f * w, 0.22f * h)
            cubicTo(0.38f * w, 0.22f * h, 0.28f * w, 0.32f * h, 0.28f * w, 0.44f * h)
            cubicTo(0.28f * w, 0.60f * h, 0.50f * w, 0.80f * h, 0.50f * w, 0.80f * h)
            cubicTo(0.50f * w, 0.80f * h, 0.72f * w, 0.60f * h, 0.72f * w, 0.44f * h)
            cubicTo(0.72f * w, 0.32f * h, 0.62f * w, 0.22f * h, 0.50f * w, 0.22f * h)
            close()
        }
        drawPath(pinPath, color = pinColor)
        drawCircle(color = holeColor, radius = 0.10f * w, center = Offset(0.50f * w, 0.44f * h))

        if (showRoute) {
            val routePath = Path().apply {
                moveTo(0.18f * w, 0.88f * h)
                cubicTo(0.30f * w, 0.96f * h, 0.70f * w, 0.96f * h, 0.82f * w, 0.88f * h)
            }
            drawPath(
                path = routePath,
                color = routeColor,
                alpha = 0.75f,
                style = Stroke(
                    width = 0.03f * w,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.02f * w, 0.16f * w))
                )
            )
        }
    }
}

/**
 * Logo de l'application "MobUB" : icone dans un badge arrondi + wordmark bicolore.
 * "Mob" en texte sombre, "UB" dans la couleur de marque (ObcampusPrimary).
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
            MobUbPinIcon(
                modifier = Modifier.size(badgeSize * 0.62f),
                pinColor = ObcampusTextWhite,
                holeColor = ObcampusPrimary,
                showRoute = true
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
 */
@Composable
fun MobUbTopBarBrand(
    modifier: Modifier = Modifier,
    onColoredBackground: Boolean = false
) {
    Row(
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
            MobUbPinIcon(
                modifier = Modifier.size(20.dp),
                pinColor = if (onColoredBackground) ObcampusPrimary else ObcampusTextWhite,
                holeColor = if (onColoredBackground) ObcampusTextWhite else ObcampusPrimary,
                showRoute = false
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