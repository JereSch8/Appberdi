package com.jackemate.appberdi.domain.entities

import java.io.Serializable

sealed class Content(
    val type: String = "",
    val tag: String = "",
    val title: String = "",
    val site: String = ""
) : Serializable {
    data class Audio(
        val href: String = "",
        val subtitle: String = "",
    ) : Content(
        tag = "Audio"
    )

    data class Image(
        val href: String = "",
        val description: String = "",
    ) : Content(
        tag = "Imagen"
    )

    data class Gif(
        val href: String = "",
        val description: String = "",
    ) : Content(
        tag = "GIF"
    )

    data class Video(
        val href: String = "",
        val description: String = "",
        val duration: String = "",
    ) : Content(
        tag = "Vídeo"
    )

    data class Text(
        val description: String = "",
    ) : Content(
        tag = "Texto"
    )

    data class Summary(
        val description: String = "",
    ) : Content(
        tag = "Resumen"
    )
}