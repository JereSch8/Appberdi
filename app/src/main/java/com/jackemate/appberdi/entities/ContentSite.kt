package com.jackemate.appberdi.entities

import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.domain.entities.Content
import java.io.Serializable

data class ContentSite(
    val description: String = "",
    val pos: GeoPoint? = null,
    val name: String = "",
    val contents: List<Content> = emptyList()
): Serializable {
    constructor(site: Site, contents: List<Content>) : this (
        site.description,
        site.pos,
        site.name,
        contents
    )
}