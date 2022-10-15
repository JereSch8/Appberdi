package com.jackemate.appberdi.data

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.entities.Site
import kotlinx.coroutines.tasks.await

class SiteRepository {
    private val db = Firebase.firestore

    fun getSites(): CollectionReference = db.collection("sites")

    suspend fun getAllSites() = try {
        db.collection("sites")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject<Site?>() }
    } catch (e: Exception) {
        Firebase.crashlytics.recordException(e)
        null
    }

    suspend fun getSite(idSite: String) = try {
        db.collection("sites")
            .document(idSite)
            .get()
            .await()
            .toObject<Site?>()
    } catch (e: Exception) {
        Firebase.crashlytics.recordException(e)
        null
    }
}