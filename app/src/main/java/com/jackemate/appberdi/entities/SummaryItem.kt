package com.jackemate.appberdi.entities

data class SummaryCountable(
    val count: Int? = null,
    val title: String = "",
    val subtitle: String = "",
)

data class SummaryYear(
    val title: String = "",
    val from: String = "",
    val to: String = "",
)

data class SummaryLabelIcon(
    val title: String = "",
    val icon: String = "", /* Se ignora por el momento */
)
