package com.jackemate.appberdi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.entities.AudioStatus
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.ui.map.MapsActivity
import com.jackemate.appberdi.ui.sites.ARG_CONTENT
import com.jackemate.appberdi.ui.view_contents.AudioActivity
import com.jackemate.appberdi.utils.*

class NotificationsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, intent.pretty())

        val notifyRepo = NotifyRepository(context.applicationContext)

        fun onAudioUpdate() {
            val time = intent.getIntExtra(AudioService.EXTRA_TIME, 0)
            val content = intent.getSerializableExtra(AudioService.EXTRA_CONTENT) as Content.Audio?
            val status = intent.getEnumExtra<AudioStatus>()

            val newNotification = notifyRepo.build {
                setContentIntent(
                    Intent(context, AudioActivity::class.java)
                        .putExtra(ARG_CONTENT, content)
                        .toPendingIntent(context)
                )

                when (status) {
                    AudioStatus.PREPARING -> {
                        setContentTitle("Descargando Audio")
                        setContentText("Perame un cachito")
                    }
                    AudioStatus.READY -> {
                        setContentTitle("Listo para escuchar")
                        setContentText(content?.title)
                    }
                    AudioStatus.PLAYING -> {
                        setContentTitle("Reproduciendo: ${content?.title}")
                        setContentText(time.toTimeString())
                    }
                    AudioStatus.PAUSED -> {
                        setContentTitle("Pausado: ${content?.title}")
                        setContentText(time.toTimeString())
                    }
                    AudioStatus.STOPPED -> {
                        setContentTitle("Terminado: ${content?.title}")
                    }
                }
            }

            notifyRepo.update(newNotification)
        }

        fun onTrackingUpdate() {
            val mode = intent.getSerializableExtra(TrackingService.EXTRA_UPDATE_MODE) as TourMode

            val newNotification = notifyRepo.build {
                setContentIntent(
                    Intent(context, MapsActivity::class.java)
                        .toPendingIntent(context)
                )

                when (mode) {
                    is TourMode.Navigating -> {
                        setContentText("Yendo a ${mode.best.title}")
                        setContentText("Estás a ${mode.distance} metros")
                    }
                    is TourMode.Selected -> {
                        setContentTitle("Yendo a ${mode.site.title}")
                    }
                    is TourMode.Thinking -> {
                        setContentTitle("¡Tour Activado!")
                        setContentText("Pensando…")
                    }
                }
            }
            notifyRepo.update(newNotification)
        }

        when (intent.action) {
            AudioService.AUDIO_UPDATES -> onAudioUpdate()
            TrackingService.TRACKING_UPDATES -> onTrackingUpdate()
        }
    }
}