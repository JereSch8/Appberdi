package com.jackemate.appberdi.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val siteRepo = SiteRepository()
    private val localInfo = LocalInfo(application)

    private val _sites: MutableLiveData<List<SiteMarker>> = MutableLiveData()
    val sites: LiveData<List<SiteMarker>> = _sites

    fun updateSites() = viewModelScope.launch(Dispatchers.IO) {
        siteRepo.getSites().addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            val result = value!!
                .mapNotNull { it.toObject<Site>() }
                .map {
                    val visited = localInfo.getDataLong(it.id) != 0L
                    SiteMarker(it, visited)
                }
            localInfo.setAmountSites(result.size)
            localInfo.setProgressSite(result.count { it.visited })
            _sites.value = result
        }
    }
}