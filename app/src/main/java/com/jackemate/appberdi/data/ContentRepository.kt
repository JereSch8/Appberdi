package com.jackemate.appberdi.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ContentRepository(context: Context) {
    private val db = Firebase.firestore
    private val storage = Firebase.s
    private val cache = CacheRepository(context)

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

    fun persistContent(content: Content) {
        val base = cache.getSiteStorageDir(content.site)

        when (content) {
            is Content.Audio -> {
                download(content.href, File(base, content.type))
            }
        }
    }
    fun download(link: String, out: File) {
        URL(link).openStream().use { input ->
            FileOutputStream(out).use { output ->
                input.copyTo(output)
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