package com.jackemate.appberdi.entities

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

sealed class Content(
    @DocumentId
    val id: String = "",
    val type: String = "",
    val tag: String = "",
    val title: String = "Sin título",
    val site: String = "",
    val idSite: String = ""
) : Serializable {
    data class Audio(
        override val href: String = "",
        val subtitle: String = "",
        val preview: Map<String, String> = emptyMap()
    ) : Cacheable, Content(tag = TAG_AUDIO)

    data class Image(
        override val href: String = "",
        val description: String = "",
    ) : Cacheable, Content(tag = TAG_IMG)

    data class Gif(
        override val href: String = "",
        val description: String = "",
    ) : Cacheable, Content(tag = TAG_GIF)

    data class Video(
        val href: String = "",
        val description: String = "",
        val duration: String = "",
    ) : Content(tag = TAG_VIDEO)

    data class Text(
        val description: String = "",
    ) : Content(tag = TAG_TEXT)

    data class Summary(
        val words: SummaryCountable? = null,
        val years: SummaryYear? = null,
        val vecinos: SummaryCountable? = null,
        val love: SummaryLabelIcon? = null,
    ) : Content(tag = TAG_SUMMARY)

    interface Cacheable {
        val href: String
        val tag: String
    }

    companion object {
        const val TAG_AUDIO = "Audios"
        const val TAG_IMG = "Imágenes"
        const val TAG_VIDEO = "Vídeos"
        const val TAG_GIF = "Gifs"
        const val TAG_TEXT = "Textos"
        const val TAG_SUMMARY = "Resumen"
    }
}