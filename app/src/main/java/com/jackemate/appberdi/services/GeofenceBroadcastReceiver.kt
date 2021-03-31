package com.jackemate.appberdi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.jackemate.appberdi.domain.entities.TourTransition
import com.jackemate.appberdi.utils.TAG
import java.lang.Exception
import java.util.*

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            val tourTransition = toTourTransition(geofenceTransition).name
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val id = triggeringGeofences.last().requestId

            Log.i(TAG, "onReceive: $geofenceTransition $triggeringGeofences")

            val editor = prefs.edit()
            editor.putString("tour_site", id)
            editor.putString("tour_transition", tourTransition)
            editor.putLong("tour_timestamp", Date().time)
            editor.apply()

            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails)
        } else {
            Log.e(TAG, "Invalid type: $geofenceTransition")
        }
    }

    private fun toTourTransition(transition: Int): TourTransition {
        return when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> TourTransition.ENTER
            Geofence.GEOFENCE_TRANSITION_EXIT -> TourTransition.EXIT
            else -> throw Exception("No valid geofence transition")
        }
    }
}