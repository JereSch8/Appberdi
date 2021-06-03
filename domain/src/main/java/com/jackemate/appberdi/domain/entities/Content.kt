package com.jackemate.appberdi.domain.entities

import java.io.Serializable

sealed class Content(
    val type: String = "",
    val tag: String = "",
    val title: String = "",
    val site: String = ""
): Serializable {
    class Audio(
        val href: String = "",
        val subtitle: String = "",
    ) : Content()

    class Image(
        val href: String = "",
        val description: String = "",
    ) : Content()

    class Gif(
        val href: String = "",
        val description: String = "",
    ) : Content()

    class Video(
        val href: String = "",
        val description: String = "",
        val subtitle: String = "",
    ) : Content()

    class Text(
        val description: String = "",
    ) : Content()
}