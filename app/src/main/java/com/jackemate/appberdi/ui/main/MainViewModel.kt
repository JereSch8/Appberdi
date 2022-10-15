package com.jackemate.appberdi.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.jackemate.appberdi.data.SiteRepository

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val siteRepo = SiteRepository()

    fun getSites() = liveData {
        emit(siteRepo.getAllSites())
    }

}