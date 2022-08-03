package com.jackemate.appberdi.data

import android.content.Context
import androidx.preference.PreferenceManager
import java.time.LocalDateTime

class PreferenceRepository(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = prefs.edit()

    private fun setDataString(key: String, value : String){
        editor.putString(key, value)
        editor.apply()
    }

    private fun getDataString(key: String): String {
        return prefs.getString(key, "none").toString()
    }

    private fun setDataInt(key: String, value : Int){
        editor.putInt(key, value)
        editor.apply()
    }

    private fun getDataInt(key: String): Int {
        return prefs.getInt(key, -8)
    }

    private fun setDataBoolean(key: String, value : Boolean){
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun getDataBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun setDataLong(key: String, value : Long){
        editor.putLong(key, value)
        editor.apply()
    }

    fun getDataLong(key: String): Long {
        return prefs.getLong(key, 0)
    }

    fun deleteAll() {
        editor.clear()
        editor.apply()
    }

    // "isnt porque por defecto la variable se inicializa en false y queda mal si se llama "is"
    fun isntFirstUsage(): Boolean = getDataBoolean("Welcome")
    fun setFirstUsage() = setDataBoolean("Welcome", true)

    fun getUserName() : String = getDataString("UserName")
    fun setUserName(name : String) = setDataString("UserName", name)

    /*
     * Limites en MB
     */
    fun getLimitStorage() : Int = getDataInt("limiteStorage").takeIf { it != -8 } ?: 150
    fun setLimitStorage(limit : Int) = setDataInt("limiteStorage", limit)

    fun getLimitMovil() : Int = getDataInt("limiteMovil").takeIf { it != -8 } ?: 150
    fun setLimitMovil(limit : Int) = setDataInt("limiteMovil", limit)

    // Cantidad de sitios obtenidos en DB
    fun getAmountSites() : Int? = getDataInt("amountSite").takeIf { it != -8 }
    fun setAmountSites(amount : Int) = setDataInt("amountSite", amount)

    fun getProgressSite() : Int? = getDataInt("progressSite").takeIf { it != -8 }
    fun setProgressSite(progress : Int) = setDataInt("progressSite", progress)

    // Cantidad de tesoros obtenidos en DB
    fun getAmountTreasures() : Int? = getDataInt("amountTreasure").takeIf { it != -8 }
    fun setAmountTreasures(amount : Int) = setDataInt("amountTreasure", amount)

    fun getProgressTreasure() : Int? = getDataInt("progressTreasure").takeIf { it != -8 }
    fun setProgressTreasure(progress : Int) = setDataInt("progressTreasure", progress)

    fun setAutoPlayAudio(b : Boolean) = setDataBoolean("autoPlayAudio", b)
    fun getAutoPlayAudio() : Boolean = getDataBoolean("autoPlayAudio")

    fun getLastAdTimestamp() = getDataString("adTimestamp")
    fun updateAdTimestamp() = setDataString("adTimestamp", LocalDateTime.now().toString())

    fun getAvatar() = getDataInt("selectedAvatar").takeIf { it != -8 }
    fun setAvatar(resource : Int) = setDataInt("selectedAvatar", resource)

    fun getVirtualMode() = getDataBoolean("virtualMode")
    fun setVirtualMode(b : Boolean) = setDataBoolean("virtualMode", b)

}