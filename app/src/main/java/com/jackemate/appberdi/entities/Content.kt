package com.jackemate.appberdi.entities

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

sealed class Content(
    @DocumentId
    val id: String = "",
    val type: String = "",
    val title: String = "Sin título",
    val site: String = "",
    val idSite: String = "",
    val tab: String = "",
) : Serializable {
    data class Audio(
        override val href: String = "",
        val subtitle: String = "",
        val preview: Map<String, String> = emptyMap()
    ) : Cacheable, Content(type = TYPE_AUDIO, tab = "Audio") {

        override fun toString(): String {
            return "Audio(href='$href')"
        }
    }

    data class Image(
        override val href: String = "",
        val description: String = "",
    ) : Cacheable, Content(type = TYPE_IMG, tab = "Imágen") {
        override fun toString(): String {
            return "Image(href='$href')"
        }
    }

    data class Gif(
        override val href: String = "",
        val description: String = "",
    ) : Cacheable, Content(type = TYPE_GIF, tab = "GIF")

    data class Video(
        val href: String = "",
        val description: String = "",
        val duration: String = "",
    ) : Content(type = TYPE_VIDEO, tab = "Vídeo")

    data class Text(
        val description: String = "",
    ) : Content(type = TYPE_TEXT, tab = "Texto") {
        override fun toString(): String {
            return "Text(description='${description.substring(0, 20)}')"
        }
    }

    data class Summary(
        val words: SummaryCountable? = null,
        val years: SummaryYear? = null,
        val vecinos: SummaryCountable? = null,
        val love: SummaryLabelIcon? = null,
    ) : Content(type = TYPE_SUMMARY, tab = "Resumen")

    interface Cacheable {
        val href: String
        val type: String
    }

    companion object {
        const val TYPE_AUDIO = "audio"
        const val TYPE_IMG = "image"
        const val TYPE_VIDEO = "video"
        const val TYPE_GIF = "gif"
        const val TYPE_TEXT = "text"
        const val TYPE_SUMMARY = "summary"
    }
}