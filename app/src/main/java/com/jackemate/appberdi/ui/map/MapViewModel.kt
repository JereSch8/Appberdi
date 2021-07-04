package com.jackemate.appberdi.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.LocalInfo
import kotlinx.coroutines.tasks.await

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SiteRepository()

    fun getSites() = liveData {
        val sites = repo.getSites().get().await()

        LocalInfo(getApplication()).setAmountSites(sites.size())

        emit(sites
            .mapNotNull { it.toObject<Site>() }
            .map {
                val visited = false // TODO buscar en local info
                SiteMarker(it, visited)
            })
    }
}