package com.jackemate.appberdi.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class CulturalActivity(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val street: String = "",
    val pos: GeoPoint? = null,
    val phone: String = "",
    val email: String = "",
    val face: Link? = null,
    val insta: Link? = null,
    val web: Link? = null,
)