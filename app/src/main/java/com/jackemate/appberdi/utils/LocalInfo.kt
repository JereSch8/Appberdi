package com.jackemate.appberdi.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import me.ibrahimsn.library.LiveSharedPreferences
import java.util.*

class LocalInfo(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = prefs.edit()

    private fun setDataBoolean(ubication: String, value : Boolean){
        editor.putBoolean(ubication, value)
        editor.apply()
    }

    private fun getDataBoolean(ubication: String): Boolean {
        return prefs.getBoolean(ubication, false)
    }

    fun isntFirstUsage(): Boolean { //"isnt porque por defecto la variable se inicializa en false y queda mal si se llama "is"
        return getDataBoolean("Welcome")
    }
    fun setFirstUsage() {
        setDataBoolean("Welcome", true)
    }
}