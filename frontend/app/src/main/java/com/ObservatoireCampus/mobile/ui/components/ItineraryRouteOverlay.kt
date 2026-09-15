package com.ObservatoireCampus.mobile.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.model.search.LegDto
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryModeStyle
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow

/** Bulle personnalisée élégante aux couleurs du trajet */
class CustomLineInfoWindow(
    mapView: MapView,
    titleText: String,
    backgroundColorArgb: Int
) : InfoWindow(createBubbleView(mapView.context, titleText, backgroundColorArgb), mapView) {

    override fun onOpen(item: Any?) {}
    override fun onClose() {}

    companion object {
        private fun createBubbleView(context: Context, text: String, bgColor: Int): android.view.View {
            val density = context.resources.displayMetrics.density
            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding((14 * density).toInt(), (10 * density).toInt(), (14 * density).toInt(), (10 * density).toInt())
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 12f * density
                    setColor(bgColor)
                    setStroke((2f * density).toInt(), android.graphics.Color.WHITE)
                }
                gravity = Gravity.CENTER
                addView(TextView(context).apply {
                    this.text = text
                    setTextColor(android.graphics.Color.WHITE)
                    textSize = 14f
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER
                })
            }
        }
    }
}

fun drawItineraryRoute(mapView: MapView, itinerary: ItineraryOptionDto): List<Polyline> {
    val polylines = itinerary.legs
        .filter { leg -> leg.geometry.size >= 2 }
        .map { leg ->
            val modeColor = ItineraryModeStyle.color(leg.mode).toArgb()
            val modeLabel = lineModeLabel(leg.mode)
            val routeName = leg.routeName?.takeIf { it.isNotBlank() } ?: ""
            val fullTitle = if (routeName.isNotEmpty()) "$modeLabel $routeName" else modeLabel

            val infoWindow = CustomLineInfoWindow(mapView, fullTitle, modeColor)

            Polyline(mapView).apply {
                val points = leg.geometry.map { point -> GeoPoint(point.lat, point.lon) }
                setPoints(points)
                outlinePaint.color = modeColor
                outlinePaint.strokeWidth = if (leg.mode.equals("WALK", true)) 6f else 9f
                if (leg.mode.equals("WALK", true)) {
                    outlinePaint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(12f, 10f), 0f)
                }

                // Ouvre la jolie bulle personnalisée exactement là où l'utilisateur clique sur la ligne
                setOnClickListener { polyline, _, eventPos ->
                    infoWindow.open(polyline, eventPos, 0, 0)
                    true
                }
            }
        }
    polylines.forEach { polyline -> mapView.overlays.add(polyline) }
    mapView.invalidate()
    return polylines
}

/**
 * Trace un itineraire depuis l'historique.
 */
fun drawHistoryRoute(mapView: MapView, item: SearchHistoryDto): List<Polyline> {
    val polylines = item.legs
        .filter { leg -> leg.fromLat != 0.0 && leg.toLat != 0.0 }
        .map { leg ->
            val modeColor = ItineraryModeStyle.color(leg.mode).toArgb()
            val modeLabel = lineModeLabel(leg.mode)
            val routeName = leg.routeName?.takeIf { it.isNotBlank() } ?: ""
            val fullTitle = if (routeName.isNotEmpty()) "$modeLabel $routeName" else modeLabel

            val infoWindow = CustomLineInfoWindow(mapView, fullTitle, modeColor)

            Polyline(mapView).apply {
                setPoints(listOf(GeoPoint(leg.fromLat, leg.fromLon), GeoPoint(leg.toLat, leg.toLon)))
                outlinePaint.color = modeColor
                outlinePaint.strokeWidth = if (leg.mode.equals("WALK", true)) 6f else 9f
                if (leg.mode.equals("WALK", true)) {
                    outlinePaint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(12f, 10f), 0f)
                }

                setOnClickListener { polyline, _, eventPos ->
                    infoWindow.open(polyline, eventPos, 0, 0)
                    true
                }
            }
        }
    polylines.forEach { polyline -> mapView.overlays.add(polyline) }
    mapView.invalidate()
    return polylines
}

fun clearItineraryRoute(mapView: MapView, polylines: List<Polyline>) {
    polylines.forEach { polyline -> mapView.overlays.remove(polyline) }
    mapView.invalidate()
}

private fun isLineLeg(mode: String): Boolean =
    mode.equals("TRAM", ignoreCase = true) || mode.equals("BUS", ignoreCase = true)

private fun lineModeLabel(mode: String): String = when {
    mode.equals("TRAM", ignoreCase = true) -> "Tram"
    mode.equals("BUS", ignoreCase = true) -> "Bus"
    mode.equals("WALK", ignoreCase = true) -> "Marche"
    else -> mode
}

fun drawItineraryLineMarkers(mapView: MapView, itinerary: ItineraryOptionDto): List<Marker> {
    val markers = itinerary.legs
        .filter { leg -> leg.geometry.size >= 2 && isLineLeg(leg.mode) }
        .map { leg -> createLineMarker(mapView, leg) }

    markers.forEach { marker -> mapView.overlays.add(marker) }
    mapView.invalidate()
    return markers
}

fun drawHistoryLineMarkers(mapView: MapView, item: SearchHistoryDto): List<Marker> {
    val markers = item.legs
        .filter { leg -> isLineLeg(leg.mode) && (leg.fromLat != 0.0 || leg.toLat != 0.0) }
        .map { leg ->
            val label = leg.routeName?.takeIf { it.isNotBlank() } ?: lineModeLabel(leg.mode)
            val midLat = (leg.fromLat + leg.toLat) / 2
            val midLon = (leg.fromLon + leg.toLon) / 2
            Marker(mapView).apply {
                position = GeoPoint(midLat, midLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = createLineBadgeDrawable(mapView.context, label, ItineraryModeStyle.color(leg.mode).toArgb())
                title = "${lineModeLabel(leg.mode)} $label"
            }
        }

    markers.forEach { marker -> mapView.overlays.add(marker) }
    mapView.invalidate()
    return markers
}

fun clearItineraryLineMarkers(mapView: MapView, markers: List<Marker>) {
    markers.forEach { marker -> mapView.overlays.remove(marker) }
    mapView.invalidate()
}

private fun createLineMarker(mapView: MapView, leg: LegDto): Marker {
    val label = leg.routeName?.takeIf { it.isNotBlank() } ?: lineModeLabel(leg.mode)
    val midPoint = leg.geometry[leg.geometry.size / 2]

    return Marker(mapView).apply {
        position = GeoPoint(midPoint.lat, midPoint.lon)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        icon = createLineBadgeDrawable(mapView.context, label, ItineraryModeStyle.color(leg.mode).toArgb())
        title = "${lineModeLabel(leg.mode)} $label"
    }
}

private fun createLineBadgeDrawable(context: Context, label: String, backgroundColorArgb: Int): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val text = label.take(6)
    val textSizePx = 13f * density
    val paddingHPx = 10f * density
    val paddingVPx = 6f * density

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = textSizePx
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    val textWidth = textPaint.measureText(text)
    val textHeight = textPaint.descent() - textPaint.ascent()

    val widthPx = (textWidth + paddingHPx * 2).toInt()
    val heightPx = (textHeight + paddingVPx * 2).toInt()

    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backgroundColorArgb }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
    }

    val rect = RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat())
    val cornerRadius = heightPx / 2f
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

    val textY = heightPx / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(text, widthPx / 2f, textY, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}