package com.ObservatoireCampus.mobile.ui.components.location

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

fun upsertUserLocationMarker(
    mapView: MapView,
    existing: Marker?,
    point: GeoPoint,
    titleText: String,
    onClick: () -> Unit
): Marker {
    val marker = existing ?: Marker(mapView).also {
        it.icon = createUserLocationDrawable(mapView.context)
        it.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        it.setOnMarkerClickListener { _, _ ->
            onClick()
            true
        }
        mapView.overlays.add(it)
    }

    marker.title = titleText
    marker.position = point
    mapView.invalidate()
    return marker
}

fun removeUserLocationMarker(mapView: MapView, marker: Marker?) {
    marker ?: return
    mapView.overlays.remove(marker)
    mapView.invalidate()
}

private fun createUserLocationDrawable(context: Context): BitmapDrawable {
    val widthDp = 40
    val heightDp = 52
    val density = context.resources.displayMetrics.density
    val widthPx = (widthDp * density).toInt()
    val heightPx = (heightDp * density).toInt()

    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val centerX = widthPx / 2f
    val headRadius = widthPx * 0.38f
    val headCenterY = headRadius + (heightPx * 0.02f)
    val tipY = heightPx.toFloat() - (heightPx * 0.02f)

    val blue = Color.rgb(25, 118, 210)
    val darkBlue = Color.rgb(13, 71, 161)

    // Ombre portée légère sous le pin
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(50, 0, 0, 0)
    }
    canvas.drawOval(
        centerX - headRadius * 0.6f, tipY - headRadius * 0.25f,
        centerX + headRadius * 0.6f, tipY + headRadius * 0.15f,
        shadowPaint
    )

    // Forme "goutte" : tête ronde + pointe triangulaire vers le bas
    val pinPath = Path().apply {
        // Triangle formant la pointe, raccordé à la tête ronde
        moveTo(centerX, tipY)
        lineTo(centerX - headRadius * 0.72f, headCenterY + headRadius * 0.55f)
        arcTo(
            centerX - headRadius, headCenterY - headRadius,
            centerX + headRadius, headCenterY + headRadius,
            125f, 290f, false
        )
        lineTo(centerX, tipY)
        close()
    }
    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = blue }
    canvas.drawPath(pinPath, pinPaint)

    // Contour légèrement plus foncé pour donner du relief
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = darkBlue
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
    }
    canvas.drawPath(pinPath, strokePaint)

    // Cercle blanc au centre de la tête (le "trou" caractéristique du pin)
    val innerWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    canvas.drawCircle(centerX, headCenterY, headRadius * 0.42f, innerWhitePaint)

    // Petit point bleu au centre du cercle blanc
    val innerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = blue }
    canvas.drawCircle(centerX, headCenterY, headRadius * 0.20f, innerDotPaint)

    return BitmapDrawable(context.resources, bitmap)
}