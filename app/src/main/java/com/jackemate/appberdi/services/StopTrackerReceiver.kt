package com.jackemate.appberdi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StopTrackerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.w("StopTrackerReceiver", "Stop services!")
        context.stopService(Intent(context, AudioService::class.java))
        context.stopService(Intent(context, TrackingService::class.java))
    }
}