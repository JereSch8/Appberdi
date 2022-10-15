package com.jackemate.appberdi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.data.NotifyRepository.Companion.READY_ID
import com.jackemate.appberdi.ui.map.MapsActivity
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.toPendingIntent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e(TAG, GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode))
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            val notifyRepo = NotifyRepository(context.applicationContext)

            val notification = notifyRepo.build {
                setContentTitle("¡Estás cerca de Alberdi, iniciá el recorrido!")
                setContentIntent(
                    Intent(context, MapsActivity::class.java)
                        .toPendingIntent(context)
                )
                setAutoCancel(true)
            }

            notifyRepo.update(notification, id = READY_ID)

//            val triggeringGeofences = geofencingEvent.triggeringGeofences
//            val id = triggeringGeofences.last().requestId

//            Log.i(TAG, "onReceive: $geofenceTransition $id $triggeringGeofences")
//            Firebase.analytics.logEvent()
        }
    }

}