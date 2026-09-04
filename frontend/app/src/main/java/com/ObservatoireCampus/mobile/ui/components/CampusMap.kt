package com.ObservatoireCampus.mobile.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.BatimentDto
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehiclePositionDto
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto
import com.ObservatoireCampus.mobile.model.station.StationTerPositionDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleTypeStyle
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingTypeStyle
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationTypeStyle
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusMap(
    campusList: List<CampusDto>,
    showPolygons: Boolean,
    languageViewModel: LanguageViewModel,
    batimentList: List<BatimentDto> = emptyList(),
    parkingList: List<ParkingPositionDto> = emptyList(),
    onParkingClick: (ParkingPositionDto) -> Unit = {},
    stationTBList: List<StationTBPositionDto> = emptyList(),
    onStationTBClick: (StationTBPositionDto) -> Unit = {},
    stationVList: List<StationVPositionDto> = emptyList(),
    onStationVClick: (StationVPositionDto) -> Unit = {},
    stationTerList: List<StationTerPositionDto> = emptyList(),
    onStationTerClick: (StationTerPositionDto) -> Unit = {},
    freeVehicleList: List<FreeVehiclePositionDto> = emptyList(),
    onFreeVehicleClick: (FreeVehiclePositionDto) -> Unit = {},
    onMapReady: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(
        campusList, batimentList, showPolygons, parkingList, stationTBList, stationVList, stationTerList, freeVehicleList, languageViewModel
    ) {
        val mapView = mapViewRef.value ?: return@LaunchedEffect
        mapView.overlays.clear()

        mapView.overlays.add(
            MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    InfoWindow.closeAllInfoWindowsOn(mapView)
                    return false
                }
                override fun longPressHelper(p: GeoPoint?): Boolean = false
            })
        )

        if (showPolygons) {
            if (campusList.isNotEmpty()) {
                drawCampusPolygons(mapView, campusList)
            }
            if (batimentList.isNotEmpty()) {
                drawBatimentPolygons(mapView, batimentList)
            }
        }

        drawParkingMarkers(mapView, parkingList, languageViewModel, coroutineScope, onParkingClick)
        drawStationTBMarkers(mapView, stationTBList, languageViewModel, coroutineScope, onStationTBClick)
        drawStationVMarkers(mapView, stationVList, languageViewModel, coroutineScope, onStationVClick)
        drawStationTerMarkers(mapView, stationTerList, languageViewModel, coroutineScope, onStationTerClick)
        drawFreeVehicleMarkers(mapView, freeVehicleList, onFreeVehicleClick)

        mapView.invalidate()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            Configuration.getInstance().userAgentValue = context.packageName
            val mapView = MapView(context)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.setBuiltInZoomControls(false)
            mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(GeoPoint(44.8067, -0.6050))
            mapViewRef.value = mapView
            onMapReady(mapView)
            mapView
        }
    )
}

private fun drawCampusPolygons(mapView: MapView, campusList: List<CampusDto>) {
    val fillColor = 0x332E7D32.toInt()   // Vert translucide
    val strokeColor = 0xFF2E7D32.toInt() // Vert soutenu

    campusList.forEach { campus ->
        // Un campus en plusieurs blocs disjoints (MultiPolygon) a une entrée par bloc.
        // On dessine CHAQUE partie comme un polygone séparé pour éviter la ligne
        // parasite qui reliait les 2 blocs quand tout était mis à plat en un seul polygone.
        campus.polygonCoordinates.forEach { part ->
            if (part.size < 3) return@forEach
            val geoPoints = part.map { coord -> GeoPoint(coord[1], coord[0]) }
            val polygon = Polygon(mapView).apply {
                points = geoPoints
                fillPaint.color = fillColor
                outlinePaint.color = strokeColor
                outlinePaint.strokeWidth = 3f
                title = campus.name
                setOnClickListener { _, _, _ ->
                    InfoWindow.closeAllInfoWindowsOn(mapView)
                    showInfoWindow()
                    true
                }
            }
            mapView.overlays.add(polygon)
        }
    }
}

/**
 * Dessine chaque bâtiment en se basant sur le helper Front-end `CampusEntity`
 * pour déterminer la couleur du remplissage et applique un contour noir fin.
 */
// Pour les bâtiments :
private fun drawBatimentPolygons(mapView: MapView, batimentList: List<BatimentDto>) {
    batimentList.forEach { batiment ->
        val fillColorHex = batiment.fillColor ?: "#EAF0D8"
        val strokeColorHex = batiment.strokeColor ?: fillColorHex

        val baseColorInt = try {
            android.graphics.Color.parseColor(fillColorHex)
        } catch (_: Exception) {
            android.graphics.Color.parseColor("#EAF0D8")
        }

        val strokeColorInt = try {
            android.graphics.Color.parseColor(strokeColorHex)
        } catch (_: Exception) {
            baseColorInt
        }

        val fillArgb = withAlpha(baseColorInt, alpha = 200)

        // Un bâtiment en plusieurs blocs disjoints a une entrée par bloc dans
        // polygonCoordinates : on dessine chaque bloc, pas seulement le premier.
        batiment.polygonCoordinates.forEach { outerRing ->
            if (outerRing.size < 3) return@forEach

            // coord[0] = longitude, coord[1] = latitude
            val geoPoints = outerRing.map { coord -> GeoPoint(coord[1], coord[0]) }

            val polygon = Polygon(mapView).apply {
                points = geoPoints
                fillPaint.color = fillArgb
                outlinePaint.color = strokeColorInt
                outlinePaint.strokeWidth = 3f
                title = batiment.name
                snippet = batiment.appartenance ?: ""

                infoWindow = CustomInfoWindow(mapView, strokeColorInt)

                setOnClickListener { _, _, _ ->
                    InfoWindow.closeAllInfoWindowsOn(mapView)
                    showInfoWindow()
                    true
                }
            }
            mapView.overlays.add(polygon)
        }
    }
}

private fun withAlpha(colorArgb: Int, alpha: Int): Int {
    return (colorArgb and 0x00FFFFFF) or (alpha shl 24)
}

private fun drawParkingMarkers(
    mapView: MapView,
    parkingList: List<ParkingPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (ParkingPositionDto) -> Unit
) {
    val context = mapView.context

    parkingList.forEach { parking ->
        val lat = parking.latitude ?: return@forEach
        val lon = parking.longitude ?: return@forEach

        val marker = Marker(mapView).apply {
            position = GeoPoint(lat, lon)
            title = parking.nom
            snippet = parking.taType

            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = ParkingTypeStyle.color(parking.taType).toArgb(),
                letter = ParkingTypeStyle.markerLetter(parking.taType)
            )
            setOnMarkerClickListener { _, _ -> onClick(parking); true }
        }

        coroutineScope.launch {
            val translatedLabel = ParkingTypeStyle.label(parking.taType, languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun drawStationTBMarkers(
    mapView: MapView,
    stations: List<StationTBPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (StationTBPositionDto) -> Unit
) {
    val context = mapView.context

    stations.forEach { station ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = station.nom
            snippet = station.mode
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = StationTypeStyle.color(station.mode).toArgb(),
                letter = StationTypeStyle.markerLetter(station.mode)
            )
            setOnMarkerClickListener { _, _ -> onClick(station); true }
        }

        coroutineScope.launch {
            val translatedLabel = StationTypeStyle.label(station.mode, languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun drawStationVMarkers(
    mapView: MapView,
    stations: List<StationVPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (StationVPositionDto) -> Unit
) {
    val context = mapView.context

    stations.forEach { station ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = station.nom
            snippet = "VELO"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = StationTypeStyle.color("VELO").toArgb(),
                letter = StationTypeStyle.markerLetter("VELO")
            )
            setOnMarkerClickListener { _, _ -> onClick(station); true }
        }

        coroutineScope.launch {
            val translatedLabel = StationTypeStyle.label("VELO", languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun drawStationTerMarkers(
    mapView: MapView,
    stations: List<StationTerPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (StationTerPositionDto) -> Unit
) {
    val context = mapView.context

    stations.forEach { station ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = station.nom
            snippet = "TER"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = StationTypeStyle.color("TER").toArgb(),
                letter = StationTypeStyle.markerLetter("TER")
            )
            setOnMarkerClickListener { _, _ -> onClick(station); true }
        }

        coroutineScope.launch {
            val translatedLabel = StationTypeStyle.label("TER", languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun createMarkerIcon(
    context: Context,
    colorArgb: Int,
    letter: String,
    sizeDp: Int = 30
): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorArgb
        style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }

    val center = sizePx / 2f
    val radius = center - (2f * density)
    canvas.drawCircle(center, center, radius, circlePaint)
    canvas.drawCircle(center, center, radius, borderPaint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = sizePx * 0.5f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    val textY = center - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(letter, center, textY, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun drawFreeVehicleMarkers(
    mapView: MapView,
    vehicles: List<FreeVehiclePositionDto>,
    onClick: (FreeVehiclePositionDto) -> Unit
) {
    val context = mapView.context
    vehicles.forEach { vehicle ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(vehicle.latitude, vehicle.longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = createVehicleMarkerIcon(context, FreeVehicleTypeStyle.color(vehicle.vehicleTypeId).toArgb(), vehicle.vehicleTypeId)
            setOnMarkerClickListener { _, _ -> onClick(vehicle); true }
        }
        mapView.overlays.add(marker)
    }
}

private fun createVehicleMarkerIcon(
    context: Context,
    colorArgb: Int,
    vehicleTypeId: String,
    sizeDp: Int = 36
): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val w = sizePx.toFloat()

    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorArgb; style = Paint.Style.FILL }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
    }

    val cx = w / 2f
    val cy = w * 0.36f
    val r = w * 0.34f
    val path = Path().apply {
        addCircle(cx, cy, r, Path.Direction.CW)
        moveTo(cx - r * 0.85f, cy + r * 0.55f)
        lineTo(cx, w * 0.95f)
        lineTo(cx + r * 0.85f, cy + r * 0.55f)
        close()
    }
    canvas.drawPath(path, pinPaint)
    canvas.drawPath(path, borderPaint)

    val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1.8f * density
        strokeCap = Paint.Cap.ROUND
    }
    val wheelR = w * 0.075f
    val wheelY = cy + w * 0.09f

    when (vehicleTypeId) {
        "yego_bike" -> {
            val leftX = cx - w * 0.13f
            val rightX = cx + w * 0.13f
            canvas.drawCircle(leftX, wheelY, wheelR, glyphPaint)
            canvas.drawCircle(rightX, wheelY, wheelR, glyphPaint)
            canvas.drawLine(leftX, wheelY, cx, cy - w * 0.05f, glyphPaint)
            canvas.drawLine(cx, cy - w * 0.05f, rightX, wheelY, glyphPaint)
            canvas.drawLine(leftX, wheelY, rightX - w * 0.02f, cy - w * 0.02f, glyphPaint)
        }

        "yego_kick" -> {
            val leftX = cx - w * 0.1f
            val rightX = cx + w * 0.14f
            canvas.drawCircle(leftX, wheelY, wheelR * 0.8f, glyphPaint)
            canvas.drawCircle(rightX, wheelY, wheelR * 0.8f, glyphPaint)
            canvas.drawLine(leftX, wheelY, rightX, wheelY, glyphPaint)
            canvas.drawLine(rightX, wheelY, rightX, cy - w * 0.12f, glyphPaint)
            canvas.drawLine(
                rightX - w * 0.05f,
                cy - w * 0.12f,
                rightX + w * 0.05f,
                cy - w * 0.12f,
                glyphPaint
            )
        }

        else -> {
            val leftX = cx - w * 0.12f
            val rightX = cx + w * 0.12f
            canvas.drawCircle(leftX, wheelY, wheelR, glyphPaint)
            canvas.drawCircle(rightX, wheelY, wheelR, glyphPaint)
            canvas.drawLine(leftX, wheelY, cx + w * 0.02f, cy - w * 0.02f, glyphPaint)
            canvas.drawLine(cx + w * 0.02f, cy - w * 0.02f, rightX, wheelY, glyphPaint)
            canvas.drawLine(
                cx - w * 0.02f,
                cy - w * 0.02f,
                cx - w * 0.02f,
                cy - w * 0.14f,
                glyphPaint
            )
        }
    }

    return BitmapDrawable(context.resources, bitmap)
}