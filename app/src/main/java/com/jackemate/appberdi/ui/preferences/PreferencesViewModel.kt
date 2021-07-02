package com.jackemate.appberdi.ui.preferences

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jackemate.appberdi.io.BasicIO.sizeStorage
import com.jackemate.appberdi.utils.LocalInfo

@SuppressLint("StaticFieldLeak")
class PreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val context = app.applicationContext

    fun getName() = LocalInfo(context).getUserName()
    fun setName(name : String) = LocalInfo(context).setUserName(name)

    fun getLimitStorage() = LocalInfo(context).getLimitStorage()
    fun setLimitStorage(limit : Int) = LocalInfo(context).setLimitStorage(limit)

    fun getSizeStorage()  = sizeStorage(context)

    fun getLimitMovil()   = LocalInfo(context).getLimitMovil()
    fun setLimitMovil(limit : Int)   = LocalInfo(context).setLimitMovil(limit)

    fun getProgressSite() : Float = if (LocalInfo(context).getProgressSite() == -8 ) 0.1f else LocalInfo(context).getProgressSite().toFloat()
    fun setProgressSite(progress : Int) = LocalInfo(context).setProgressSite(progress)

    fun getProgressTreasure() : Float = if (LocalInfo(context).getProgressTreasure() == -8 ) 0.1f else LocalInfo(context).getProgressTreasure().toFloat()

    fun getAmountSites() : Float = if (LocalInfo(context).getAmountSites() == -8 ) 0.1f else LocalInfo(context).getAmountSites().toFloat()

    fun getAmountTreasure() : Float = if (LocalInfo(context).getAmountTreasures() == -8 ) 0.1f else LocalInfo(context).getAmountTreasures().toFloat()

    fun getAutoPlayAudio() = LocalInfo(context).getAutoPlayAudio()
    fun setAutoPlayAudio(isChecked : Boolean) = LocalInfo(context).setAutoPlayAudio(isChecked)

    fun getAutoPlayVideo() = LocalInfo(context).getAutoPlayVideo()
    fun setAutoPlayVideo(isChecked : Boolean) = LocalInfo(context).setAutoPlayVideo(isChecked)

    fun getAvatarResource() = LocalInfo(context).getAvatar()

}