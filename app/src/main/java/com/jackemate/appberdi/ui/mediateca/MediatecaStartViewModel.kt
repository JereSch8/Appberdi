package com.jackemate.appberdi.ui.mediateca

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.entities.ContentMediateca
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediatecaStartViewModel(app: Application) : AndroidViewModel(app) {
    private val repoContent = ContentRepository(app)

    private val _contents : MutableLiveData<List<ContentMediateca>> = MutableLiveData()
    val contents: LiveData<List<ContentMediateca>> = _contents

    fun getContents() = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentMediateca().addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _contents.value = value!!.mapNotNull { it.toObject() }
        }
    }

}