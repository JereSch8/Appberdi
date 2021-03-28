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

enum class TourTransition { ENTER, EXIT }

data class TourEvent(
    val site: String,
    val transition: TourTransition
)