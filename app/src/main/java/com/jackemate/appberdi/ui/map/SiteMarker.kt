package com.jackemate.appberdi.ui.map

import com.jackemate.appberdi.entities.Position
import com.jackemate.appberdi.entities.Site
import java.io.Serializable
import java.util.*

data class SiteMarker(
    val id: String,
    val title: String,
    val pos: Position,
    val accessible: Boolean,
    val visited: Boolean,
    val lastVisited: Date?
): Serializable {
    constructor(s: Site, lastVisited: Long) : this(
        s.id,
        s.name,
        Position(s.pos!!),
        s.accessible,
        lastVisited != 0L,
        Date(lastVisited)
    )
}