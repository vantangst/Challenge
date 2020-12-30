package com.co.challengeliv3ly.models

import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.IconMarker
import com.google.android.gms.maps.model.LatLng

class PointArea(
    val id: Int,
    val title_: String,
    val address_: String,
    val coordinate: LatLng,
    val icon: Int? = null
) : IconMarker {
    override fun getIcon(): Int {
        return icon ?: R.drawable.ic_location_dot
    }

    override fun getIconPosition(): LatLng {
        return coordinate
    }

    override fun getAddress(): String {
        return address_
    }

    override fun getTitle(): String {
        return title_
    }

}