package com.jackemate.appberdi.data

import android.util.Log
import com.jackemate.appberdi.domain.entities.Site
import com.jackemate.appberdi.domain.repository.Repo


class RepoImplement(private val locationRepo : LocationRepository) : Repo {

    override suspend fun getAllSites(): List<Site> {
        return locationRepo.getAllSite()
    }

    override suspend fun getSites(query: String): List<Site> {
        TODO("Not yet implemented")
    }

    override suspend fun getTour() {
        TODO("Not yet implemented")
    }

    override suspend fun getAttraction() {
        TODO("Not yet implemented")
    }

    override suspend fun getContent() {
        TODO("Not yet implemented")
    }


}