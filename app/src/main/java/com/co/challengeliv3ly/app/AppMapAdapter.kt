package com.co.challengeliv3ly.app

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.addMarker
import com.co.challengeliv3ly.extensions.zoomCameraToBound
import com.co.challengeliv3ly.utils.MapUtil

class AppMapAdapter<T : IconMarker>(fragment: SupportMapFragment) : BaseMapAdapter(fragment) {

    private val mIconSize: Int = resources.getDimensionPixelSize(R.dimen.size_40)
    private val mMarkers = arrayListOf<Marker>()
    private var mPolylines: MutableList<Polyline> = arrayListOf()


    var onItemClickListener: ((T) -> Unit)? = null

    var items: MutableList<out T>? = null
        set(value) {
            if (value == null) {
                clearAll()
                return
            }
            field = value
            showIcons(value)
        }

    override fun onMapReady(map: GoogleMap) {
        println("onMapReady")
        map.setOnMarkerClickListener { marker ->
            onItemClickListener?.invoke(marker.tag as T)
            true
        }
        map.setOnMapClickListener {
            moveToLocation(it)
        }
    }

    private fun clearAll() {
        mMarkers.forEach { it.remove() }
        mMarkers.clear()
    }

    /**
     * Show list icons as marker in map
     */
    private fun showIcons(value: MutableList<out IconMarker>) {
        launch { map ->
            clearAll()
            val builder = LatLngBounds.Builder()
            value.forEach {
                showIcon(map, it, it.getTitle())
                builder.include(it.getIconPosition())
            }
            if (value.size == 1) {
                MapUtil.moveCameraZoomTo(map, value[0].getIconPosition())
            } else {
                zoomCameraToBound(builder.build())
            }
        }
    }

    private fun showIcon(map: GoogleMap, icon: IconMarker, title: String) {
        mMarkers.add(
            context.addMarker(map, icon.getIconPosition(), title, icon.getIcon()).also { it.tag = icon })
    }

    fun addPolyline(path: List<LatLng>) {
        if (mPolylines.size > 0) {
            for (line in mPolylines) line.remove()
        }
        mPolylines = arrayListOf()
        mPolylines.add(googleMap!!.addPolyline(PolylineOptions().addAll(path).color(Color.RED)))
    }
}

interface IconMarker {
    fun getIcon(): Int

    fun getIconPosition(): LatLng

    fun getTitle(): String

    fun getAddress(): String
}