package com.jackemate.appberdi.entities

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Position(val lat: Double, val lon: Double): Serializable {

    constructor(geoPoint: GeoPoint): this(geoPoint.latitude, geoPoint.longitude)

    fun toLatLng() = LatLng(lat, lon)

}