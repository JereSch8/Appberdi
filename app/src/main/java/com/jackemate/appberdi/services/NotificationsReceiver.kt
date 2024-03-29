package com.jackemate.appberdi.services

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import com.jackemate.appberdi.BuildConfig
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.entities.AudioStatus
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.services.TrackingService.Companion.TRACKING_STOP
import com.jackemate.appberdi.ui.map.MapsActivity
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.ui.shared.contents.activities.AudioActivity
import com.jackemate.appberdi.ui.sites.SiteActivity
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

                if (!content?.idSite.isNullOrEmpty()) {
                    setContentIntent(
                        Intent(context, SiteActivity::class.java)
                            .apply { putExtra("idSite", content?.idSite) }
                            .toPendingIntent(context)
                    )
                } else {
                    setContentIntent(
                        Intent(context, AudioActivity::class.java)
                            .putExtra(ARG_CONTENT, content)
                            .toPendingIntent(context)
                    )
                }

                when (status) {
                    AudioStatus.PREPARING -> {
                        setContentTitle("Descargando audio")
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

                addAction(
                    R.drawable.ic_close,
                    context.getString(R.string.opciones_stop),
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(TRACKING_STOP).apply {
                            setPackage(BuildConfig.APPLICATION_ID)
                        },
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            PendingIntent.FLAG_IMMUTABLE
                        else 0
                    )
                )

                when (mode) {
                    is TourMode.Navigating -> {
                        setContentTitle("${mode.best.title} está mas cerca")
                        setContentText("Estás a ${mode.distance} metros")
                    }
                    is TourMode.Selected -> {
                        setContentTitle("Yendo a ${mode.site.title}")
                        setContentText("Estás a ${mode.distance} metros")
                    }
                    is TourMode.Thinking -> {
                        setContentTitle("¡Tour Activado!")
                        setContentText("Pensando…")
                    }
                    is TourMode.Ready -> {
                        setContentTitle("Ya estás en ${mode.site.title}")
                        setContentText("¡Abrí la app para iniciar el recorrido!")
                        priority = PRIORITY_MAX
                        setContentIntent(
                            Intent(context, SiteActivity::class.java)
                                .apply { putExtra("idSite", mode.site.id) }
                                .toPendingIntent(context)
                        )
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