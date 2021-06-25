package com.jackemate.appberdi.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Site(
    @DocumentId
    var id: String = "",
    val description: String = "",
    val pos: GeoPoint? = null,
    val name: String = "",
    val accessible: Boolean = false,
    val tour: List<DocumentReference> = emptyList()
)