package com.jackemate.appberdi.ui.mediateca

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediatecaViewModel(val context: Application) : AndroidViewModel(context) {
    private val repoContent = ContentRepository(context)

    private val _contents : MutableLiveData<List<Content>> = MutableLiveData()
    val contents: LiveData<List<Content>> = _contents

    fun getContents(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentBySite(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _contents.value = value!!.mapNotNull { repoContent.fromDoc(it) }
        }
    }



}

