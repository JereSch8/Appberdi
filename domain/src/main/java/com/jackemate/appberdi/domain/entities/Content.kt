package com.jackemate.appberdi.domain.entities

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