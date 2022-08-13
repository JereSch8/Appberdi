package com.jackemate.appberdi.ui.sites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.tasks.await
import java.util.*

class SiteViewModel(application: Application) : AndroidViewModel(application) {
    private val siteRepo = SiteRepository()
    private val contentRepo = ContentRepository()
    private val localInfo = PreferenceRepository(application)

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

    fun setVisited(idSite: String) {
        localInfo.setDataLong(idSite, Date().time)
    }

    fun getName() = localInfo.getUserName()
}