package com.co.challengeliv3ly.app

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.co.challengeliv3ly.utils.MapUtil

@SuppressLint("MissingPermission")
open class BaseMapAdapter(val fragment: SupportMapFragment) {
    val context = fragment.requireContext()
    val resources = context.resources!!
    private var mMap: GoogleMap? = null
    private var mShouldMove: Boolean = false
    private var mCurrentLocation: LatLng? = null
    private val mOnMapReadyListeners = hashSetOf<(GoogleMap) -> Unit>()
    private lateinit var mClient: FusedLocationProviderClient

    //val currentLocation get() = mMap?.cameraPosition?.target
    val googleMap get() = mMap

    private lateinit var mMyLocation: Location

    var isPermissionAllowed = false
        set(value) {
            if (value == field) return
            field = value
            launch {
                it.isMyLocationEnabled = value
            }
        }

    init {
        mClient = context.let { LocationServices.getFusedLocationProviderClient(it) }!!
        fragment.getMapAsync { map ->
            mMap = map
            onMapReady(map)
        }

        val task = mClient.lastLocation
        task.addOnSuccessListener { location ->
            try {
                mCurrentLocation = location.latLng
            } catch (e: Exception) {e.printStackTrace()}
            //map.uiSettings.isMyLocationButtonEnabled = false
            mOnMapReadyListeners.apply {
                forEach { mMap?.let { it1 -> it(it1) } }
                clear()
            }
        }
    }

    protected open fun onMapReady(map: GoogleMap) {}

    fun moveToMyLocation() {
        launch {
            val marker = it.addMarker(
                MarkerOptions()
                    .position(mCurrentLocation!!)
                    .draggable(true)
            )
            MapUtil.moveCameraZoomTo(it, mCurrentLocation!!)

        }
    }

    /**
     * Move camera to location on map
     * @param it android.location.Location
     */

    fun moveToLocation(it: Location) {
        moveToLocation(it.latLng)
    }

    /**
     * Move camera to location on map
     * @param it com.google.android.gms.maps.model.LatLng
     */
    fun moveToLocation(it: LatLng) {
        launch { map ->
            if (mCurrentLocation == null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM))
            } else {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM))
            }
        }
        mCurrentLocation = it
    }

    /**
     * Zoom out camera
     */
    fun zoomOut() {
        zoom(mMap!!.cameraPosition.zoom - 2)
    }

    /**
     * Zoom in camera
     */
    fun zoomIn() {
        zoom(mMap!!.cameraPosition.zoom + 2)
    }

    protected fun launch(function: (GoogleMap) -> Unit) {
        if (mMap == null) mOnMapReadyListeners.add(function)
        else function(mMap!!)
    }

    private fun zoom(zoom: Float) {
        if (mMap == null) return
        if (mCurrentLocation == null) mCurrentLocation = mMap!!.cameraPosition.target
        mMap!!.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(mMap!!.cameraPosition.target, zoom)
        )
    }

    fun capture(function: (Bitmap) -> Unit) {
        launch {
            it.snapshot(function)
        }
    }

    companion object {
        const val DEFAULT_ZOOM = 15f
    }
}

private val Location.latLng get() = LatLng(latitude, longitude)
