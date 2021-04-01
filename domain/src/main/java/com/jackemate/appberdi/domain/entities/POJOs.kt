package com.jackemate.appberdi.domain.entities

data class Site(
    var id: String = "",
    val description : String = "",
    val latlong : Any = Any(),
    val name : String = ""
)

data class ContentAudio(
    val title: String = "",
    val href: String = "",
    val site: String = "",
    val type: String = "",
    val subtitle: String = ""
)

data class ContentImage(
    val href: String = "",
    val site: String = "",
    val type: String = "",
    val description: String = ""
)

data class ContentGif(
    val title: String = "",
    val href: String = "",
    val site: String = "",
    val type: String = "",
    val description: String = ""
)

data class ContentVideo(
    val title: String = "",
    val href: String = "",
    val site: String = "",
    val type: String = "",
    val description: String = "",
    val subtitle : String = ""
)

data class ContentText(
    val title: String = "",
    val site: String = "",
    val type: String = "",
    val description: String = ""
)

enum class TourTransition { ENTER, EXIT }

data class TourEvent(
    val site: String,
    val transition: TourTransition
)

data class Link(val href: String = "", val rel: String = "")

data class Attraction(
    val name: String = "",
    val description: String = "",
    val links: List<Link> = emptyList(),
    val site: String? = null,
    val horarios: Any = Any()
)