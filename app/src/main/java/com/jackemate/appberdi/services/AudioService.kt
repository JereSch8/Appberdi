package com.jackemate.appberdi.services

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.jackemate.appberdi.data.CacheRepository
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.data.NotifyRepository.Companion.NOTIFICATION_ID
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.toTimeString

class AudioService : LifecycleService() {
    val mediaPlayer: MediaPlayer = MediaPlayer()
    private val binder = TourServiceBinder()
    private val notifyRepo = NotifyRepository(this)
    private val cacheRepo = CacheRepository(this)

    private var content: Content.Audio? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    inner class TourServiceBinder : Binder() {
        val service: AudioService get() = this@AudioService
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, notifyRepo.audioRunning())
        setListeners()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent ?: return START_STICKY
        when (intent.action) {
            ACTION_SELECT -> actionSelect(intent.getSerializableExtra("content") as Content.Audio)
            ACTION_PLAY -> actionPlay()
            ACTION_SEEK -> actionSeek(intent.getIntExtra(EXTRA_POSITION, -1))
            ACTION_SEEK_BY -> actionSeekBy(intent.getIntExtra(EXTRA_OFFSET, -1))
            ACTION_FORCE -> actionForce()
        }
        return START_STICKY
    }

    fun actionSelect(audio: Content.Audio) {
        Log.i(TAG, "actionSelect: $audio")

        // Si es el mismo audio, no hago nada
        if (content?.href == audio.href) {
            broadcast()
            return
        }

        content = audio
        notifyRepo.audioRunning()

        // Parar lo que se esté reproduciendo y volver a empezar
        mediaPlayer.reset()
        broadcast(AUDIO_PREPARING)

        observe(cacheRepo.get(audio)) { file ->
            Log.i(TAG, "input file: $file")
            val uri = Uri.fromFile(file)
            Log.i(TAG, "input uri: $uri")

            mediaPlayer.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(applicationContext, uri)
                Log.i(TAG, "preparing async: $uri")
                prepareAsync()
            }
        }
    }

    fun actionPlay() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            notifyRepo.paused(mediaPlayer.currentPosition.toTimeString())
            handler.removeCallbacks(runnable)
            broadcast()
        } else {
            mediaPlayer.start()
            notifyRepo.playing(content!!)
            handler.post(runnable)
            broadcast()
        }

    }

    fun actionSeek(position: Int) {
        if (position == -1) {
            Log.e(TAG, "actionSeek: position == -1")
        }
        mediaPlayer.seekTo(position.coerceIn(0, mediaPlayer.duration))
    }

    fun actionSeekBy(offset: Int) {
        val position = mediaPlayer.currentPosition + offset
        mediaPlayer.seekTo(position.coerceIn(0, mediaPlayer.duration))
    }

    fun actionForce() = broadcast()

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

    private fun setListeners() {
        Log.d(TAG, "initMediaPlayer")
        mediaPlayer.apply {
            // Evitar que el celu entre en modo hibernación mientras reproduce audio
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setOnPreparedListener {
                Log.i(TAG, "setOnPreparedListener")
                broadcast(AUDIO_READY)
            }
            setOnSeekCompleteListener {
                Log.d(TAG, "setOnSeekCompleteListener")
                broadcast()
            }
            setOnCompletionListener {
                Log.d(TAG, "setOnCompletionListener")
                notifyRepo.running()
                handler.removeCallbacks(runnable)
                broadcast(AUDIO_STOPPED)
            }
            setOnErrorListener { _: MediaPlayer, a: Int, b: Int ->
                Log.e(TAG, "OnErrorListener: a: $a, b: $b")
                true
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
                notifyRepo.playing(content!!, time)
                broadcast()
                handler.postDelayed(this, 1000)
            }
        }
    }

    companion object {
        const val ACTION_SELECT = "com.jackemate.appberdi.action.SELECT"
        const val ACTION_PLAY = "com.jackemate.appberdi.action.PLAY"
        const val ACTION_SEEK = "com.jackemate.appberdi.action.SEEK"
        const val ACTION_SEEK_BY = "com.jackemate.appberdi.action.SEEK_BY"
        const val ACTION_FORCE = "com.jackemate.appberdi.action.FORCE"

        const val EXTRA_OFFSET = "offset"
        const val EXTRA_POSITION = "seek"

        const val BROADCAST_UPDATES = "com.jackemate.TourService.STATUS"
        const val BROAD_PROGRESS_UPDATE = "com.jackemate.appberdi.action.PROGRESS"

        const val AUDIO_PREPARING = 0 // todo
        const val AUDIO_READY = 1 // TODO
        const val AUDIO_PLAYING = 2
        const val AUDIO_PAUSED = 3
        const val AUDIO_STOPPED = 4
    }
}