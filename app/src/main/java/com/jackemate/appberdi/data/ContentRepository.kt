package com.jackemate.appberdi.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG

class ContentRepository {
    private val db = Firebase.firestore

    private fun getContentWhere(idSite : String) = db.collection("contents").whereEqualTo("site",idSite)

    fun fromDoc(doc: DocumentSnapshot): Content? {
        return when (doc["type"]) {
            "image" -> doc.toObject<Content.Image>()
            "audio" -> doc.toObject<Content.Audio>()
            "video" -> doc.toObject<Content.Video>()
            "text" -> doc.toObject<Content.Text>()
            "summary" -> doc.toObject<Content.Summary>()
            else -> {
                Log.w(TAG, "fromDoc: type desconocido: $doc")
                null
            }
        }
    }

    fun getContentMediateca() = db.collection("mediateca")

    fun getContentAudioWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "audio")
    fun getContentImageWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "image")
    fun getContentGifWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "gif")
    fun getContentVideoWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "video")
    fun getContentTextWhere(idSite : String) = getContentWhere(idSite).whereEqualTo("type", "text")

    fun getTags() = listOf("Texto", "Audio", "Imagen", "Gif", "Video")

}