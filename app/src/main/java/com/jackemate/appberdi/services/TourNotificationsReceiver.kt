package com.jackemate.appberdi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.utils.TAG

class TourNotificationsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, intent.toString())
        val notifyRepo = NotifyRepository(context.applicationContext)
        val mode = intent.getSerializableExtra("mode") as TourMode
        when (mode) {
            is TourMode.Navigating -> {
                notifyRepo.tourStatus(mode.best.title, mode.distance)
            }
        }
    }

}