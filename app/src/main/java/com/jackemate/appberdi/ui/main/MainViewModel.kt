package com.jackemate.appberdi.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.data.TourRepository
import com.jackemate.appberdi.domain.entities.Site
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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