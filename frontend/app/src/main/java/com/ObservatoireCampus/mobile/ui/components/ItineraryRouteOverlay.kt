// ui/components/ItineraryRouteOverlay.kt
package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.ui.graphics.toArgb
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.ui.components.itinerary.ItineraryModeStyle
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

fun drawItineraryRoute(mapView: MapView, itinerary: ItineraryOptionDto): List<Polyline> {
    val polylines = itinerary.legs
        .filter { leg -> leg.geometry.size >= 2 } // ignore les legs sans géométrie exploitable
        .map { leg ->
            Polyline(mapView).apply {
                val points = leg.geometry.map { point -> GeoPoint(point.lat, point.lon) }
                setPoints(points)
                outlinePaint.color = ItineraryModeStyle.color(leg.mode).toArgb()
                outlinePaint.strokeWidth = if (leg.mode.equals("WALK", true)) 6f else 9f
                if (leg.mode.equals("WALK", true)) {
                    outlinePaint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(12f, 10f), 0f)
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