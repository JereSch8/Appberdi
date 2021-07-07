package com.jackemate.appberdi.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class SiteRepository {
    private val db = Firebase.firestore

    fun getSites(): CollectionReference = db.collection("sites")

    fun getSite(idSite: String): DocumentReference = db.collection("sites").document(idSite)

    fun clearVisited() {

    }

    suspend fun getVisited() {
        val sites = getSites().get().await()

    }
}