package com.ObservatoireCampus.mobile.ui.components

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import com.ObservatoireCampus.mobile.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayWithIW
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(
    mapView: MapView,
    private val strokeColorInt: Int
) : InfoWindow(R.layout.layout_custom_info_window, mapView) {

    override fun onOpen(item: Any?) {
        // Récupération sécurisée du Polygon / Overlay cliqué via le paramètre item
        val overlay = item as? OverlayWithIW
        val title = overlay?.title ?: ""
        val snippet = overlay?.snippet ?: ""

        val txtTitle = mView.findViewById<TextView>(R.id.info_title)
        val txtSub = mView.findViewById<TextView>(R.id.info_subtitle)
        val container = mView.findViewById<View>(R.id.info_window_container)

        txtTitle.text = title
        if (snippet.isNotBlank()) {
            txtSub.text = snippet
            txtSub.visibility = View.VISIBLE
        } else {
            txtSub.visibility = View.GONE
        }

        // Fond de la bulle en blanc pur + bordure assortie à la couleur du bâtiment
        val shape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            setStroke(5, strokeColorInt)
            cornerRadius = 24f
        }
        container.background = shape

        mView.setOnClickListener { close() }
    }

    override fun onClose() {}
}