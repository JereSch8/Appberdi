package com.jackemate.appberdi.ui.map

import com.google.android.gms.maps.model.LatLng
import com.jackemate.appberdi.entities.Site

class SiteMarker(
    val id: String,
    val title: String,
    val pos: LatLng,
    val accessible: Boolean,
    val visited: Boolean
) {
    constructor(s: Site, visited: Boolean) : this(
        s.id,
        s.name,
        LatLng(s.pos!!.latitude, s.pos.longitude),
        s.accessible,
        visited
    )
}