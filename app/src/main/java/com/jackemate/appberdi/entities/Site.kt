package com.jackemate.appberdi.entities

import com.google.firebase.firestore.GeoPoint

data class Site(
    var id: String = "",
    val description : String = "",
    val latlong : GeoPoint? = null,
    val name : String = ""
)
