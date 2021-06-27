package com.jackemate.appberdi.utils

import android.content.Context
import androidx.preference.PreferenceManager
import java.time.LocalDateTime

class LocalInfo(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = prefs.edit()

    private fun setDataString(ubication: String, value : String){
        editor.putString(ubication, value)
        editor.apply()
    }

    private fun getDataString(ubication: String): String {
        return prefs.getString(ubication, "none").toString()
    }

    private fun setDataInt(ubication: String, value : Int){
        editor.putInt(ubication, value)
        editor.apply()
    }

    private fun getDataInt(ubication: String): Int {
        return prefs.getInt(ubication, -8)
    }

    private fun setDataBoolean(ubication: String, value : Boolean){
        editor.putBoolean(ubication, value)
        editor.apply()
    }

    private fun getDataBoolean(ubication: String): Boolean {
        return prefs.getBoolean(ubication, false)
    }

    private fun setDataLong(key: String, value : Long){
        editor.putLong(key, value)
        editor.apply()
    }

    private fun getDataLong(key: String): Long {
        return prefs.getLong(key, 0)
    }


    //"isnt porque por defecto la variable se inicializa en false y queda mal si se llama "is"
    fun isntFirstUsage(): Boolean = getDataBoolean("Welcome")

    fun setFirstUsage() = setDataBoolean("Welcome", true)

    fun getUserName() : String = getDataString("UserName")

    fun setUserName(name : String) = setDataString("UserName", name)

    fun getLimitStorage() : Int = getDataInt("limiteStorage")          //limites en MB

    fun setLimitStorage(limit : Int) = setDataInt("limiteStorage", limit)//limites en MB

    fun getLimitMovil() : Int = getDataInt("limiteMovil")          //limites en MB

    fun setLimitMovil(limit : Int) = setDataInt("limiteMovil", limit)//limites en MB

    fun getAmountSites() : Int = getDataInt("amountSite")           //Cantidad de sitios obtenidos en DB

    fun setAmountSites(amount : Int) = setDataInt("amountSite", amount)

    fun getProgressSite() : Int = getDataInt("progressSite")

    fun setProgressSite(progress : Int) = setDataInt("progressSite", progress)

    fun getAmountTreasures() : Int = getDataInt("amountTreasure")    //Cantidad de tesoros obtenidos en DB

    fun setAmountTreasures(amount : Int) = setDataInt("amountTreasure", amount)

    fun getProgressTreasure() : Int = getDataInt("progressTreasure")

    fun setProgressTreasure(progress : Int) = setDataInt("progressTreasure", progress)

    fun setAutoPlayAudio(b : Boolean) = setDataBoolean("autoPlayAudio", b)

    fun getAutoPlayAudio() : Boolean = getDataBoolean("autoPlayAudio")

    fun setAutoPlayVideo(b : Boolean) = setDataBoolean("autoPlayVideo", b)

    fun getAutoPlayVideo() : Boolean = getDataBoolean("autoPlayVideo")

    fun getLastAdTimestamp() = getDataString("AdTimestamp")
    fun updateAdTimestamp() = setDataString("AdTimestamp", LocalDateTime.now().toString())
}