package com.jackemate.appberdi.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jackemate.appberdi.R
import com.jackemate.appberdi.ui.map.MapsActivity

class NotifyRepository(val context: Context) {

    fun remove(id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(id)
        }
    }

    fun update(id: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, notification)
        }
    }

    fun playing(curr: String = "00:00") =
        update(NOTIFICATION_ID, makeForeground("Reproduciendo algo", curr))

    fun paused(curr: String = "00:00") =
        update(NOTIFICATION_ID, makeForeground("Audio pausado", curr))

    fun running() =
        update(NOTIFICATION_ID, tourRunning())

    fun tourRunning() =
        makeForeground("¡Tour Activado!")

    fun tourStatus(site: String, distance: Int) =
        update(NOTIFICATION_ID, makeForeground("Yendo a $site", "Estás a $distance metros"))

    fun makeForeground(
        title: String = "Cargando",
        text: String? = null,
        progress: Boolean = false,
        showWhen: Boolean = false
    ): Notification {

        // Ante la duda, nos aseguramos de que el channel está creado
        createBackgroundChannel()

        val intent = Intent(context, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            context, System.currentTimeMillis().toInt(), intent, 0
        )

        return NotificationCompat.Builder(context, CHANNEL_BACKGROUND_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setShowWhen(showWhen)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_check)
            .also {
                if (progress) it.setProgress(100, 0, true)
            }
            .setContentIntent(pendingIntent)
            .build()
    }

    fun createBackgroundChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.channel_name)
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_BACKGROUND_ID, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_BACKGROUND_ID = "background"
        const val NOTIFICATION_ID = 536218216
    }
}