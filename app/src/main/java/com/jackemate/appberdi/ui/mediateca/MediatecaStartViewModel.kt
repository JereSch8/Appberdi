package com.jackemate.appberdi.ui.mediateca

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.jackemate.appberdi.data.ContentRepository

class MediatecaStartViewModel(app: Application) : AndroidViewModel(app) {
    private val repoContent = ContentRepository()

    fun getItems() = liveData {
        emit(repoContent.getMediateca())
    }

}