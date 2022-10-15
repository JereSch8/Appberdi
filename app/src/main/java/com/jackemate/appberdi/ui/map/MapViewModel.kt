package com.jackemate.appberdi.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.TourMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val siteRepo = SiteRepository()
    private val preferenceRepo = PreferenceRepository(application)

    private val _sites: MutableLiveData<List<SiteMarker>?> = MutableLiveData()
    val sites: LiveData<List<SiteMarker>?> = _sites

    private val _mode: MutableLiveData<TourMode> = MutableLiveData()
    val mode: LiveData<TourMode> = _mode

    fun getVirtualMode() = preferenceRepo.getVirtualMode()
    fun setVirtualMode(b: Boolean) = preferenceRepo.setVirtualMode(b)

    fun updateSites() = viewModelScope.launch(Dispatchers.IO) {
        val sites = siteRepo.getAllSites()
            ?.map { SiteMarker(it, preferenceRepo.getDataLong(it.id)) }
            ?.also { sites ->
                preferenceRepo.setAmountSites(sites.size)
                preferenceRepo.setProgressSite(sites.count { it.visited })
            }

        _sites.postValue(sites)
    }

    fun setMode(mode: TourMode) {
        _mode.postValue(mode)
    }

    // Trigger update with same value
    fun triggerModeUpdate() {
        mode.value?.let(::setMode)
    }

}