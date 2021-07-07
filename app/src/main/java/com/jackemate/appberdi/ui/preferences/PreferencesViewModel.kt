package com.jackemate.appberdi.ui.preferences

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.io.BasicIO.sizeStorage

@SuppressLint("StaticFieldLeak")
class PreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val context = app.applicationContext
    private val preferenceRepo = PreferenceRepository(context)

    fun getName() = preferenceRepo.getUserName()
    fun setName(name: String) = preferenceRepo.setUserName(name)

    fun getLimitStorage() = preferenceRepo.getLimitStorage()
    fun setLimitStorage(limit: Int) = preferenceRepo.setLimitStorage(limit)

    fun getSizeStorage() = sizeStorage(context)

    fun getLimitMovil() = preferenceRepo.getLimitMovil()
    fun setLimitMovil(limit: Int) = preferenceRepo.setLimitMovil(limit)

    fun getProgressSite(): Float =
        if (preferenceRepo.getProgressSite() == -8) 0.1f else PreferenceRepository(
            context
        ).getProgressSite().toFloat()

    fun setProgressSite(progress: Int) = preferenceRepo.setProgressSite(progress)

    fun getProgressTreasure(): Float =
        if (preferenceRepo.getProgressTreasure() == -8) 0.1f else PreferenceRepository(
            context
        ).getProgressTreasure().toFloat()

    fun getAmountSites(): Float =
        if (preferenceRepo.getAmountSites() == -8) 0.1f else PreferenceRepository(
            context
        ).getAmountSites().toFloat()

    fun getAmountTreasure(): Float =
        if (preferenceRepo.getAmountTreasures() == -8) 0.1f else PreferenceRepository(
            context
        ).getAmountTreasures().toFloat()

    fun getAutoPlayAudio() = preferenceRepo.getAutoPlayAudio()
    fun setAutoPlayAudio(isChecked: Boolean) =
        preferenceRepo.setAutoPlayAudio(isChecked)

    fun getAutoPlayVideo() = preferenceRepo.getAutoPlayVideo()
    fun setAutoPlayVideo(isChecked: Boolean) =
        preferenceRepo.setAutoPlayVideo(isChecked)

    fun getAvatarResource() = preferenceRepo.getAvatar()

}