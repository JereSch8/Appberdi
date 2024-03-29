package com.jackemate.appberdi.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jackemate.appberdi.data.CacheRepository
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.entities.Content
import kotlinx.coroutines.launch

class PreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val preferenceRepo = PreferenceRepository(app.applicationContext)
    private val cacheRepo = CacheRepository(app.applicationContext)

    private val _data: MutableLiveData<PreferenceData> = MutableLiveData()
    val data: LiveData<PreferenceData> = _data

    fun load() = viewModelScope.launch {
        _data.value = PreferenceData(
            username = preferenceRepo.getUserName(),
            avatar = preferenceRepo.getAvatar(),
            storageLimit = preferenceRepo.getLimitStorage(),
            storageSize = getStorageSize(),
            mobilLimit = preferenceRepo.getLimitMovil(),
            siteProgress = preferenceRepo.getProgressSite()?.toFloat() ?: 0.1f,
            siteTotal = preferenceRepo.getAmountSites()?.toFloat() ?: 12f,
            treasureProgress = preferenceRepo.getProgressTreasure()?.toFloat() ?: 0.1f,
            treasureTotal = preferenceRepo.getAmountTreasures()?.toFloat() ?: 2f,
            autoPlayAudio = preferenceRepo.getAutoPlayAudio(),
        )
    }

    private fun getStorageSize() = cacheRepo.sizeOf(Content.TYPE_IMG) +
            cacheRepo.sizeOf(Content.TYPE_AUDIO)

    fun setName(name: String) = preferenceRepo.setUserName(name)
    fun setLimitStorage(limit: Int) = preferenceRepo.setLimitStorage(limit)
    fun setLimitMovil(limit: Int) = preferenceRepo.setLimitMovil(limit)
    fun setAutoPlayAudio(isChecked: Boolean) = preferenceRepo.setAutoPlayAudio(isChecked)

    // Limpiar, pero dejar unas cositas
    fun clearData() {
        val name = preferenceRepo.getUserName()
        preferenceRepo.deleteAll()
        preferenceRepo.setUserName(name)
        preferenceRepo.setFirstUsage()
    }

}