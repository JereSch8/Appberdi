package com.jackemate.appberdi.entities

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class ContentSite(
    val idSite: String = "",
    val description: String = "",
    val pos: GeoPoint? = null,
    val name: String = "",
    val contents: List<Content> = emptyList()
): Serializable {
    constructor(site: Site, contents: List<Content>) : this (
        site.id,
        site.description,
        site.pos,
        site.name,
        contents
    )
}