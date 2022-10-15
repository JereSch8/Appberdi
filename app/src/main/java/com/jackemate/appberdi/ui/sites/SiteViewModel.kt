package com.jackemate.appberdi.ui.sites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.utils.TAG
import java.util.*

class SiteViewModel(application: Application) : AndroidViewModel(application) {
    private val siteRepo = SiteRepository()
    private val contentRepo = ContentRepository()
    private val localInfo = PreferenceRepository(application)

    fun getSite(idSite: String) = liveData {
        val content = siteRepo.getSite(idSite)?.let { site ->
            Log.i(TAG, "site tour size: ${site.tour.size}")

            val contents = site.tour.mapNotNull { contentRepo.get(it) }
            Log.i(TAG, "site parsed contents size: ${contents.size}")
            ContentSite(site, contents)
        }

        emit(content)
    }

    fun setVisited(idSite: String) {
        localInfo.setDataLong(idSite, Date().time)
    }

    fun getName() = localInfo.getUserName()
}