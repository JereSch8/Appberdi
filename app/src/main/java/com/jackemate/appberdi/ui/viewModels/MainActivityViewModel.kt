package com.jackemate.appberdi.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jackemate.appberdi.domain.repository.Repo
import kotlinx.coroutines.Dispatchers

class MainActivityViewModel(repo : Repo) : ViewModel() {

    val requestAllSite = liveData(Dispatchers.IO) {
        emit(repo.getAllSites())
    }

}