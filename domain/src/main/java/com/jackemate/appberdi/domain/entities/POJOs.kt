package com.jackemate.appberdi.domain.entities

data class Site(
    val description : String ,
    val latlon : String,
    val name : String
)

data class Content(
    val title: String,
    val description: String,
    val href: String,
    val site: String,
    val type: String
)
