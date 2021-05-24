package com.jackemate.appberdi.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.data.TourRepository
import com.jackemate.appberdi.entities.Site
import kotlinx.coroutines.tasks.await

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val siteRepo = SiteRepository()
    private val tourRepo = TourRepository(app)

    fun getSites() = liveData {
        val docs = siteRepo.getSites().get().await()
        val list = docs.documents.mapNotNull {
            val site: Site? = it.toObject()
            site?.id = it.id
            return@mapNotNull site
        }
        emit(list)
    }

    fun tourEvents() = tourRepo.tourEvents()
}