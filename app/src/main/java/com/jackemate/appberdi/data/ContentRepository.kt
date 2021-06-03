package com.jackemate.appberdi.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.domain.entities.Content

class ContentRepository {
    private val db = Firebase.firestore

    private fun getContentWhere(idSite : String) = db.collection("contents").whereEqualTo("site",idSite)

    fun fromDoc(doc: DocumentSnapshot): Content? {
        return when (doc["type"]) {
            "image" -> doc.toObject<Content.Image>()
            "audio" -> doc.toObject<Content.Audio>()
            "video" -> doc.toObject<Content.Video>()
            "text" -> doc.toObject<Content.Text>()
            else -> null
        }
    }

    fun getContentAudioWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "audio")
    fun getContentImageWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "image")
    fun getContentGifWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "gif")
    fun getContentVideoWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "video")
    fun getContentTextWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "text")

    fun getTags() = listOf("Texto", "Audio", "Imagen", "Gif", "Video")
    fun getSites() = listOf("Antiguo mercado","Casa de la FUC", "Cementerio  San Jerónimo", "Centro cultural La Piojera",
        "Club Belgrano", "Escuela Manuel Belgrano", "Esquina del Chango Rodriguez", "Esquina del Cordobazo",
        "Hospital Nacional de Clínicas", "Museo de la Reforma", "Pasaje de la Reforma", "Plaza Colón", "")
}