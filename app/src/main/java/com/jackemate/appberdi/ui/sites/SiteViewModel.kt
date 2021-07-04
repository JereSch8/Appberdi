package com.jackemate.appberdi.ui.sites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.tasks.await

class SiteViewModel : ViewModel() {
    private val siteRepo = SiteRepository()
    private val contentRepo = ContentRepository()

    fun getSite(idSite: String) = liveData {
        val doc = siteRepo.getSite(idSite).get().await()
        val site = doc.toObject<Site?>()

        site?.let {
            Log.i(TAG, "site tour size: ${site.tour.size}")
            val contents = site.tour.mapNotNull {
                contentRepo.fromDoc(it.get().await())
            }
            Log.i(TAG, "site parsed contents size: ${contents.size}")
            emit(ContentSite(site, contents))
        }
    }
}