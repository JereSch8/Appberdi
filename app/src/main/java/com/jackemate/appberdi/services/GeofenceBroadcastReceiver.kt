package com.jackemate.appberdi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.jackemate.appberdi.utils.TAG

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e(TAG, GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode))
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val id = triggeringGeofences.last().requestId

            Log.i(TAG, "onReceive: $geofenceTransition $id $triggeringGeofences")

            // TODO: mandar notificación de que el usuario está cerca de un sitio!
            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails)
        }
    }

}