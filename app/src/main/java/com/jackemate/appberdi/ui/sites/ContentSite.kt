package com.jackemate.appberdi.ui.sites

import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.entities.Site

data class ContentSite(
    val description: String = "",
    val pos: GeoPoint? = null,
    val name: String = "",
    val contents: List<Content> = emptyList()
) {
    constructor(site: Site, contents: List<Content>) : this (
        site.description,
        site.pos,
        site.name,
        contents
    )
}