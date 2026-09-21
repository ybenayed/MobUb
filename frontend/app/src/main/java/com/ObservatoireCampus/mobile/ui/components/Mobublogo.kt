package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.R
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusTextWhite
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextDark
import com.ObservatoireCampus.mobile.ui.theme.WaypusTextMuted

@Composable
fun MobUbPinIcon(
    modifier: Modifier = Modifier,
    pinColor: Color = ObcampusTextWhite,
    holeColor: Color = ObcampusPrimary,
    routeColor: Color = ObcampusTextWhite,
    showRoute: Boolean = true
) {
    Image(
        painter = painterResource(id = R.drawable.logo_mobub),
        contentDescription = "Logo MobUB",
        modifier = modifier
    )
}

@Composable
fun MobUbLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = true,
    tagline: String = "Mobilite intelligente du campus de Bordeaux",
    badgeSize: Dp = 64.dp,
    wordmarkFontSize: TextUnit = 26.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MobUbPinIcon(modifier = Modifier.size(badgeSize * 2.5f))

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

@Composable
fun MobUbTopBarBrand(
    modifier: Modifier = Modifier,
    onColoredBackground: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onColoredBackground) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObcampusTextWhite),
                contentAlignment = Alignment.Center
            ) {
                MobUbPinIcon(modifier = Modifier.size(40.dp))
            }
            Text(
                text = "MobUB",
                color = ObcampusTextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            MobUbPinIcon(modifier = Modifier.size(48.dp))
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