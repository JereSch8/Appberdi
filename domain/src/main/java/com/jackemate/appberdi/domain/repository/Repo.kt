package com.jackemate.appberdi.domain.repository

import com.jackemate.appberdi.domain.entities.Site

interface Repo {

    suspend fun getAllSites() : List<Site>

    suspend fun getSites(query : String) : List<Site>

    suspend fun getTour()

    suspend fun getAttraction()

    suspend fun getContent()

}
