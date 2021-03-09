package com.jackemate.appberdi.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SiteRepository {
    private val db = Firebase.firestore

    fun getSites(): CollectionReference = db.collection("sites")

    companion object {
        const val TAG: String = "LocationRepository"
    }
}