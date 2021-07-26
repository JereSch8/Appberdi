package com.jackemate.appberdi.ui.map

import com.google.android.gms.maps.model.LatLng
import com.jackemate.appberdi.entities.Site
import java.util.*

data class SiteMarker(
    val id: String,
    val title: String,
    val pos: LatLng,
    val accessible: Boolean,
    val visited: Boolean,
    val lastVisited: Date?
) {
    constructor(s: Site, lastVisited: Long) : this(
        s.id,
        s.name,
        LatLng(s.pos!!.latitude, s.pos.longitude),
        s.accessible,
        lastVisited != 0L,
        Date(lastVisited)
    )
}