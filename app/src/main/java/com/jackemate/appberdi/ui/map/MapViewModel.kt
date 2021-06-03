package com.jackemate.appberdi.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val repo = SiteRepository()

    private val _sites: MutableLiveData<List<SiteMarker>> = MutableLiveData()
    val sites: LiveData<List<SiteMarker>> = _sites

    fun fetchSites() = viewModelScope.launch(Dispatchers.IO) {
        repo.getSites().addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _sites.value = value!!
                .mapNotNull { it.toObject<Site>() }
                .map {
                    val visited = false // TODO buscar en local info
                    SiteMarker(it, visited)
                }
        }
    }
}