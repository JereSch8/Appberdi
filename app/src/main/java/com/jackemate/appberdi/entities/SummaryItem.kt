package com.jackemate.appberdi.entities

import java.io.Serializable

data class SummaryCountable(
    val count: Int? = null,
    val title: String = "",
    val subtitle: String = "",
) : Serializable

data class SummaryYear(
    val title: String = "",
    val from: String = "",
    val to: String = "",
) : Serializable

data class SummaryLabelIcon(
    val title: String = "",
    val icon: String = "", /* Se ignora por el momento */
) : Serializable
