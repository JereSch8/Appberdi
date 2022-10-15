package com.jackemate.appberdi.data

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.entities.ContentMediateca
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.tasks.await

class ContentRepository {
    private val db = Firebase.firestore

    suspend fun get(ref: DocumentReference) = try {
        fromDoc(ref.get().await())
    } catch (e: Exception) {
        null
    }

    suspend fun getBySite(site: String) = try {
        db.collection("contents")
            .whereEqualTo("site", site)
            .get()
            .await()
            .documents
            .mapNotNull { fromDoc(it) }
    } catch (e: Exception) {
        null
    }

    private fun fromDoc(doc: DocumentSnapshot): Content? {
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

    suspend fun getMediateca(): List<ContentMediateca>? = try {
        db.collection("mediateca")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject() }
    } catch (e: Exception) {
        null
    }

}