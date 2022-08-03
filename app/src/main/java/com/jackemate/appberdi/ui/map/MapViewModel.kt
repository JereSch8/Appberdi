package com.jackemate.appberdi.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val siteRepo = SiteRepository()
    private val localInfo = PreferenceRepository(application)

    private val _sites: MutableLiveData<List<SiteMarker>> = MutableLiveData()
    val sites: LiveData<List<SiteMarker>> = _sites

    private val _mode: MutableLiveData<TourMode> = MutableLiveData()
    val mode: LiveData<TourMode> = _mode

    fun getVirtualMode() = localInfo.getVirtualMode()
    fun setVirtualMode(b: Boolean) = localInfo.setVirtualMode(b)

    fun updateSites() = viewModelScope.launch(Dispatchers.IO) {
        val response = siteRepo.getSites().get().await()
        if (response == null) {
            Log.w(TAG, "Listen failed.")
            return@launch
        }
        val result = response.documents
            .mapNotNull { it.toObject<Site>() }
            .map { SiteMarker(it, localInfo.getDataLong(it.id)) }

        localInfo.setAmountSites(result.size)
        localInfo.setProgressSite(result.count { it.visited })
        _sites.postValue(result)
    }

    fun setMode(mode: TourMode) {
        _mode.postValue(mode)
    }

    // Trigger update with same value
    fun triggerModeUpdate() {
        mode.value?.let(::setMode)
    }

}