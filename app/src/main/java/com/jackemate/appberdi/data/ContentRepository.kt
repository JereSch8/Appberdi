package com.jackemate.appberdi.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ContentRepository {
    private val db = Firebase.firestore


    private fun getContentWhere(idSite : String) = db.collection("contents").whereEqualTo("site",idSite)

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