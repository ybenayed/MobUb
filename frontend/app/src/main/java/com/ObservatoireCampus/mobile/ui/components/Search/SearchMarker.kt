package com.ObservatoireCampus.mobile.ui.components.search

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable


fun createSearchResultMarkerIcon(context: Context): BitmapDrawable {
    val widthDp = 36
    val heightDp = 46
    val density = context.resources.displayMetrics.density
    val widthPx = (widthDp * density).toInt()
    val heightPx = (heightDp * density).toInt()

    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val centerX = widthPx / 2f
    val headRadius = widthPx * 0.4f
    val headCenterY = headRadius + (heightPx * 0.02f)
    val tipY = heightPx.toFloat() - (heightPx * 0.02f)

    val red = Color.rgb(220, 38, 38)

    val pinPath = Path().apply {
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
    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = red }
    canvas.drawPath(pinPath, pinPaint)

    val innerWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    canvas.drawCircle(centerX, headCenterY, headRadius * 0.32f, innerWhitePaint)

    return BitmapDrawable(context.resources, bitmap)
}