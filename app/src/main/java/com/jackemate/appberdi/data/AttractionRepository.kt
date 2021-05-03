package com.jackemate.appberdi.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AttractionRepository {
    private val db = Firebase.firestore

    fun getAttractions(): CollectionReference = db.collection("atracciones")

    fun get(id: String): DocumentReference = db.collection("atracciones").document(id)
}