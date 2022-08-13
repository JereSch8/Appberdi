package com.jackemate.appberdi.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Site
import kotlinx.coroutines.tasks.await

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val siteRepo = SiteRepository()

    fun getSites() = liveData {
        val docs = siteRepo.getSites().get().await()
        val list = docs.documents.mapNotNull { it.toObject<Site?>() }
        emit(list)
    }

}