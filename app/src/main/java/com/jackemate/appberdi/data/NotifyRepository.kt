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
import com.jackemate.appberdi.ui.main.MainActivity
import com.jackemate.appberdi.ui.sites.SiteActivity

class NotifyRepository(val context: Context) {

    fun remove(id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(id)
        }
    }

    private fun update(notification: Notification, id: Int = NOTIFICATION_ID) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, notification)
        }
    }

    fun playing(curr: String = "00:00", idSite: String) =
        update(makeForeground(
            "Reproduciendo algo",
            curr,
            intent = Intent(context, SiteActivity::class.java).putExtra("idSite", idSite)))

    fun paused(curr: String = "00:00") =
        update(makeForeground("Audio pausado", curr))

    fun running() =
        update(tourRunning())

    fun tourRunning() =
        makeForeground("¡Tour Activado!", "Pensando…")

    fun tourStatus(site: String, distance: Int) =
        update(makeForeground("Yendo a $site", "Estás a $distance metros"))

    fun tourSelected(site: String) =
        update(makeForeground("Yendo a $site"))

    fun makeForeground(
        title: String = "Cargando",
        text: String? = null,
        progress: Boolean = false,
        showWhen: Boolean = false,
        intent: Intent = Intent(context, MainActivity::class.java)
    ): Notification {

        // Ante la duda, nos aseguramos de que el channel está creado
        createBackgroundChannel()

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent.also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            0
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