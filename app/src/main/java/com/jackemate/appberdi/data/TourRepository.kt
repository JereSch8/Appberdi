package com.jackemate.appberdi.data

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.preference.PreferenceManager
import com.jackemate.appberdi.domain.entities.TourEvent
import com.jackemate.appberdi.domain.entities.TourTransition
import me.ibrahimsn.library.LiveSharedPreferences

class TourRepository(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val liveSharedPreferences = LiveSharedPreferences(preferences)

    fun tourEvents() = MediatorLiveData<TourEvent>().apply {
        addSource(liveSharedPreferences.getLong("tour_timestamp", 0)) { value ->
            if (value != 0L) {
                val site = preferences.getString("tour_site", "")!!
                val transition = preferences.getString("tour_transition", "")!!
                this.value = TourEvent(site, TourTransition.valueOf(transition))

                // Reset timestamp
                val editor = preferences.edit()
                editor.remove("tour_timestamp")
                editor.apply()
            }
        }
    }
}