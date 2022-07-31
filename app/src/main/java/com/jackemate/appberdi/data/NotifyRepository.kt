package com.jackemate.appberdi.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jackemate.appberdi.R

class NotifyRepository(val context: Context) {

    fun remove(id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(id)
        }
    }

    fun update(notification: Notification, id: Int = DEFAULT_ID) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, notification)
        }
    }

    fun foreground(): Notification {
        createBackgroundChannel()
        return build {
            setContentTitle("Iniciando cosillas")
        }
    }

    fun build(block: NotificationCompat.Builder.() -> Unit): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_BACKGROUND_ID)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.mipmap.ic_launcher)

        block(builder)

        return builder.build()
    }

    private fun createBackgroundChannel() {
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
        const val DEFAULT_ID = 536218216
        const val READY_ID = 432143214
    }
}