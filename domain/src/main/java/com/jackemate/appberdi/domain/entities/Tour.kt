package com.jackemate.appberdi.domain.entities

enum class TourTransition { ENTER, EXIT }

data class TourEvent(
    val site: String,
    val transition: TourTransition
)