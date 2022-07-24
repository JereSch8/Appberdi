package com.jackemate.appberdi.ui.preferences

data class PreferenceData(
    val username: String,
    val avatar: Int?,

    val storageLimit: Int,
    val storageSize: Double,

    val mobilLimit: Int,

    val siteProgress: Float,
    val siteTotal: Float,

    val treasureProgress: Float,
    val treasureTotal: Float,

    val autoPlayAudio: Boolean,
)