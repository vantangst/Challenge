package com.co.challengeliv3ly.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.co.challengeliv3ly.extensions.AppConst.MAP.DEFAULT_ZOOM_CAMERA
import java.io.IOException
import java.util.*


object MapUtil {

    fun moveCameraTo(map: GoogleMap, location: LatLng, func: (()-> Unit)) {
        map!!.moveCamera(CameraUpdateFactory.newLatLng(location))
        func()
    }

    fun moveCameraZoomTo(map: GoogleMap, location: LatLng) {
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_CAMERA))
    }

    fun animateCameraTo(map: GoogleMap, location: LatLng) {
        map!!.animateCamera(CameraUpdateFactory.newLatLng(location))
    }

    fun animateCameraZoomTo(map: GoogleMap, location: LatLng) {
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_CAMERA))
    }

    fun createMarkerIconBitMap(map: GoogleMap, latLng: LatLng, bitmap: Bitmap): Marker? {
        val marker = map!!.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        )
        return marker
    }

    fun createMarkerDefault(map: GoogleMap, latLng: LatLng): Marker? {
        val marker = map!!.addMarker(
            MarkerOptions()
                .position(latLng)
        )
        return marker
    }

    fun createMarkerIconResource(map: GoogleMap, latLng: LatLng, idDrawable: Int): Marker? {

        val marker = map!!.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(idDrawable))
                .draggable(true)
        )
        return marker
    }

    fun createMarkerIconVector(
        context: Context,
        map: GoogleMap,
        latLng: LatLng,
        idDrawable: Int
    ): Marker? {
        val icon = bitmapDescriptorFromVector(context, idDrawable)
        val marker = map!!.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(icon)
                //.icon(BitmapDescriptorFactory.fromResource(idDrawable))
                .draggable(true)
        )
        return marker
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun removeAllMarker(markers: MutableList<Marker>) {
        if (markers.size == 0) return
        for (marker in markers) {
            marker.remove()
        }
    }

    fun getMarkerFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun addBounds(markers: ArrayList<Marker>): LatLngBounds {
        val builder = LatLngBounds.Builder()
        for (marker in markers) {
            builder.include(marker.position)
        }
        return builder.build()
    }

    fun zoomCameraToBound(map: GoogleMap, mapFragment: SupportMapFragment, bounds: LatLngBounds) {
        if (mapFragment != null && mapFragment.view != null) {
            val zoom: Int = getBoundsZoomLevel(
                bounds.northeast, bounds.southwest,
                mapFragment.view!!.measuredWidth,
                mapFragment.view!!.measuredHeight
            )
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, zoom))
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    bounds.center,
                    zoom - 1.toFloat()
                )
            )
        }
    }

    fun getBoundsZoomLevel(northeast: LatLng, southwest: LatLng, width: Int, height: Int): Int {
        val GLOBE_WIDTH = 256 // a constant in Google's map projection
        val ZOOM_MAX = 21
        val latFraction =
            (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI
        val lngDiff = northeast.longitude - southwest.longitude
        val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360
        val latZoom = zoom(height.toDouble(), GLOBE_WIDTH.toDouble(), latFraction)
        val lngZoom = zoom(width.toDouble(), GLOBE_WIDTH.toDouble(), lngFraction)
        val zoom =
            Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX.toDouble())
        return (zoom - 2).toInt()
    }

    private fun latRad(lat: Double): Double {
        val sin = Math.sin(lat * Math.PI / 180)
        val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
    }

    private fun zoom(mapPx: Double, worldPx: Double, fraction: Double): Double {
        val LN2 = .693147180559945309417
        return Math.log(mapPx / worldPx / fraction) / LN2
    }

    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder: Geocoder
        val listAddress: List<Address>
        var address: String = ""
        try {
            geocoder = Geocoder(context, Locale.getDefault())
            listAddress = geocoder.getFromLocation(latitude, longitude, 1)
            if (listAddress.isNotEmpty()) {
                address = listAddress[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
}
