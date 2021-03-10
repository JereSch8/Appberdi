package com.jackemate.appberdi.domain.entities

data class Site(
    val description : String = "" ,
    val latlong : Any = "" ,
    val name : String = ""
)

private interface Content {
    val title: String
    val href: String
    val site: String
    val type: String
}

data class ContentAudio(
    override val title: String = "",
    override val href: String = "",
    override val site: String = "",
    override val type: String = "",
    val subtitle: String = ""
) : Content

data class ContentGif(
    override val title: String = "",
    override val href: String = "",
    override val site: String = "",
    override val type: String = "",
    val description: String = ""
) : Content