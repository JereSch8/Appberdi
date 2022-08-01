package com.jackemate.appberdi.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CulturalRepository {
    private val db = Firebase.firestore

    fun getAll() = db.collection("actividades")
}