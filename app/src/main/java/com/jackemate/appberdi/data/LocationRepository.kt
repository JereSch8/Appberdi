package com.jackemate.appberdi.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.domain.entities.Site

class LocationRepository(context : Context) {
    val TAG : String = "LocationRepository"

    private val db = Firebase.firestore

     fun getAllSite(): List<Site> {
        val listSite : MutableList<Site> = arrayListOf()
        db.collection("sites").get().addOnSuccessListener { result ->
            for (document in result) {
                val site = Site(document["description"] as String, "${document["latlong"]}", document["name"] as String  )
                listSite.add(site)

                Log.e(TAG, "${document.id} => description=${site.description}, latlon=${site.latlon} y name=${site.name}")
            }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
         Log.e(TAG, "listSite.size: ${listSite.size}" )
         return listSite
    }


}