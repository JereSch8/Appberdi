package com.jackemate.appberdi.ui.mediateca

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.jackemate.appberdi.data.ContentRepository

class MediatecaSiteViewModel(val context: Application) : AndroidViewModel(context) {
    private val repoContent = ContentRepository()

    fun getContents(idSite: String) = liveData {
        emit(repoContent.getBySite(idSite))
    }

}
