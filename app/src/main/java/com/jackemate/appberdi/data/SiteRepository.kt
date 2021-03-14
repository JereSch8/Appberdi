package com.jackemate.appberdi.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SiteRepository {
    private val db = Firebase.firestore

    fun getSites(): CollectionReference = db.collection("sites")

    private fun getContentWhere(idSite : String, type : String) = db.collection("contents")
                            .whereEqualTo("site",idSite)
                            .whereEqualTo("type",type)

    fun getContentAudioWhere(idSite : String) = getContentWhere(idSite,"audio")
    fun getContentImageWhere(idSite : String) = getContentWhere(idSite,"image")
    fun getContentGifWhere(idSite : String) = getContentWhere(idSite,"gif")

    companion object {
        const val TAG: String = "LocationRepository"
    }
}