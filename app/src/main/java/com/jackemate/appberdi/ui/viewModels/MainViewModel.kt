package com.jackemate.appberdi.ui.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.models.Site
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repo = SiteRepository()

    private val _sites : MutableLiveData<List<Site>> = MutableLiveData()
    val sites: LiveData<List<Site>> = _sites

    fun getSites() = viewModelScope.launch(Dispatchers.IO) {

        repo.getSites().addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _sites.value = value!!.mapNotNull { it.toObject() }
        }
    }

    companion object {
        const val TAG = "MainActivityViewModel"
    }
}