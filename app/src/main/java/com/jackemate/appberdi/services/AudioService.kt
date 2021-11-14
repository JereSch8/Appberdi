package com.jackemate.appberdi.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.data.NotifyRepository.Companion.NOTIFICATION_ID
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.toTimeString

class AudioService : Service() {
    val mediaPlayer: MediaPlayer by lazy { MediaPlayer.create(this, R.raw.a) }
    private val binder = TourServiceBinder()
    private val notifyRepo = NotifyRepository(this)

    private var content: Content.Audio? = null

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class TourServiceBinder : Binder() {
        val service: AudioService get() = this@AudioService
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, notifyRepo.tourRunning())
        initMediaPlayer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_SELECT -> {
                content = intent.getSerializableExtra("content") as Content.Audio
                // TODO init
            }
            ACTION_PLAY -> {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    notifyRepo.paused(mediaPlayer.currentPosition.toTimeString())
                    handler.removeCallbacks(runnable)
                    broadcast()
                } else {
                    mediaPlayer.start()
                    notifyRepo.playing(idSite = content!!.idSite)
                    handler.post(runnable)
                    broadcast()
                }
            }
            ACTION_SEEK -> {
                val seek = intent.getIntExtra(EXTRA_SEEK, -1).takeIf { it != -1 }
                val change = intent.getIntExtra(EXTRA_OFFSET, 0)

                val position = seek ?: mediaPlayer.currentPosition + change
                mediaPlayer.seekTo(position.coerceIn(0, mediaPlayer.duration))
                broadcast()
            }
            ACTION_FORCE -> broadcast()
        }
        return START_STICKY
    }

    override fun onLowMemory() {
        super.onLowMemory()
        release()
    }

    override fun onDestroy() {
        release()
        stopSelf()
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    private fun release() {
        try {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
            broadcast(AUDIO_STOPPED)
        } catch (_: Exception) {
        }
    }

    private fun broadcast(status: Int? = null) {
        val broadcast = Intent()
        broadcast.action = BROAD_PROGRESS_UPDATE
        broadcast.putExtra("time", mediaPlayer.currentPosition)
        broadcast.putExtra("status", status ?: playingOrPaused())
        broadcast.putExtra("duration", mediaPlayer.duration)
        broadcast.setPackage("com.jackemate.appberdi")
        sendBroadcast(broadcast)
    }

    private fun initMediaPlayer() {
        Log.d(TAG, "initMediaPlayer")
        mediaPlayer.apply {
//            setDataSource()
//            prepareAsync()
            // Evitar que el celu entre en modo hibernación mientras reproduce audio
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setOnPreparedListener {
                Log.d(TAG, "setOnPreparedListener")
            }
            setOnSeekCompleteListener {
                Log.d(TAG, "setOnSeekCompleteListener")
                broadcast(AUDIO_STOPPED)
            }
            setOnCompletionListener {
                Log.d(TAG, "setOnCompletionListener")
                notifyRepo.running()
                handler.removeCallbacks(runnable)
                broadcast(AUDIO_STOPPED)
            }
            setOnErrorListener { _: MediaPlayer, _: Int, _: Int ->
                TODO("Not yet implemented")
            }
        }
    }

    private fun playingOrPaused() = if (mediaPlayer.isPlaying) AUDIO_PLAYING else AUDIO_PAUSED

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val time = mediaPlayer.currentPosition.toTimeString()
                Log.d("TourService", "runnable: $time")
                notifyRepo.playing(time, content!!.idSite)
                broadcast()
                handler.postDelayed(this, 1000)
            }
        }
    }

    companion object {
        const val ACTION_SELECT = "com.jackemate.appberdi.action.SELECT"
        const val ACTION_PLAY = "com.jackemate.appberdi.action.PLAY"
        const val ACTION_SEEK = "com.jackemate.appberdi.action.SEEK"
        const val ACTION_FORCE = "com.jackemate.appberdi.action.FORCE"
        const val BROADCAST_UPDATES = "com.jackemate.TourService.STATUS"

        const val EXTRA_OFFSET = "offset"
        const val EXTRA_SEEK = "seek"

        const val BROAD_PROGRESS_UPDATE = "com.jackemate.appberdi.action.PROGRESS"
        const val AUDIO_READY = 0 // TODO
        const val AUDIO_PLAYING = 1
        const val AUDIO_PAUSED = 2
        const val AUDIO_STOPPED = 3
    }
}