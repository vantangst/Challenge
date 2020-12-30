package com.co.challengeliv3ly.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.BaseMapAdapter
import kotlin.math.ln
import kotlin.math.sin


fun Context.resize(resizeIcon: Int, drawableRes: Int): Bitmap {
    val imageBitmap = getBitmapResource(drawableRes)
    return Bitmap.createScaledBitmap(imageBitmap, resizeIcon, resizeIcon, false)
}

fun Context.getBitmapResource(drawableRes: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableRes)
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return bitmap
}

fun BaseMapAdapter.zoomCameraToBound(mBounds: LatLngBounds) {
    val zoom = getBoundsZoomLevel(
            mBounds.northeast, mBounds.southwest,
            fragment.view!!.measuredWidth,
            fragment.view!!.measuredHeight
    )
    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, zoom - 8))
    googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mBounds.center, zoom.toFloat()))
}

fun Context.addMarker(map: GoogleMap, latLng: LatLng, icon: Int? = R.drawable.ic_marker): Marker {
    val sizeIcon: Int
    val marker: Marker?
    val resource = resources

    sizeIcon = resource.getDimension(R.dimen.size_30).toInt()
    marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                            resize(
                                    sizeIcon,
                                    icon!!
                            )
                    )
                )
    )
    marker?.showInfoWindow()
    return marker
}

fun Context.addMarker(map: GoogleMap, latLng: LatLng, title: String?, icon: Int? = R.drawable.ic_marker): Marker {
    val sizeIcon: Int
    val marker: Marker?
    val resource = resources

    sizeIcon = resource.getDimension(R.dimen.size_30).toInt()
    marker = map.addMarker(
        MarkerOptions()
            .position(latLng)
            .icon(
                BitmapDescriptorFactory.fromBitmap(
                    resize(
                        sizeIcon,
                        icon!!
                    )
                )
            )
            .title(title)
    )
    marker?.showInfoWindow()
    return marker
}

fun getBoundsZoomLevel(northeast: LatLng, southwest: LatLng, width: Int, height: Int): Int {
    val GLOBE_WIDTH = 256 // a constant in Google's map projection
    val ZOOM_MAX = 21
    val latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI
    val lngDiff = northeast.longitude - southwest.longitude
    val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360
    val latZoom = zoom(height.toDouble(), GLOBE_WIDTH.toDouble(), latFraction)
    val lngZoom = zoom(width.toDouble(), GLOBE_WIDTH.toDouble(), lngFraction)
    val zoom = latZoom.coerceAtMost(lngZoom).coerceAtMost(ZOOM_MAX.toDouble())
    return (zoom - 2).toInt()
}

private fun latRad(lat: kotlin.Double): kotlin.Double {
    val sin = sin(lat * Math.PI / 180)
    val radX2 = ln((1 + sin) / (1 - sin)) / 2
    return radX2.coerceAtMost(Math.PI).coerceAtLeast(-Math.PI) / 2
}

private fun zoom(mapPx: kotlin.Double, worldPx: kotlin.Double, fraction: kotlin.Double): kotlin.Double {
    val LN2 = .693147180559945309417
    return ln(mapPx / worldPx / fraction) / LN2
}