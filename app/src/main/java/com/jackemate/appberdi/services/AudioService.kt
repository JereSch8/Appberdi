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
import com.jackemate.appberdi.data.NotifyRepository.Companion.DEFAULT_ID
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.entities.AudioStatus
import com.jackemate.appberdi.entities.AudioStatus.*
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.putExtra

class AudioService : LifecycleService() {
    val mediaPlayer: MediaPlayer = MediaPlayer()
    private val binder = TourServiceBinder()
    private val notifyRepo = NotifyRepository(this)
    private val preferenceRepo by lazy { PreferenceRepository(this) }
    private val cacheRepo by lazy { CacheRepository(this) }

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
        startForeground(DEFAULT_ID, notifyRepo.foreground())

        Log.d(TAG, "onCreate: initMediaPlayer")
        mediaPlayer.apply {
            // Evitar que el celu entre en modo hibernación mientras reproduce audio
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setOnPreparedListener {
                Log.i(TAG, "setOnPreparedListener")
                broadcast(READY)
                if (preferenceRepo.getAutoPlayAudio()) {
                    actionPlay()
                }
            }
            setOnSeekCompleteListener {
                Log.d(TAG, "setOnSeekCompleteListener")
                broadcast()
            }
            setOnCompletionListener {
                Log.d(TAG, "setOnCompletionListener")
                handler.removeCallbacks(runnable)
                broadcast(STOPPED)
            }
            setOnErrorListener { _: MediaPlayer, a: Int, b: Int ->
                Log.e(TAG, "OnErrorListener: a: $a, b: $b")
                true
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(DEFAULT_ID, notifyRepo.foreground())

        when (intent?.action) {
            ACTION_SELECT -> actionSelect(intent.getSerializableExtra("content") as Content.Audio)
            ACTION_PLAY -> actionPlay()
            ACTION_SEEK -> actionSeek(intent.getIntExtra(EXTRA_POSITION, -1))
            ACTION_SEEK_BY -> actionSeekBy(intent.getIntExtra(EXTRA_OFFSET, -1))
            ACTION_FORCE -> actionForce()
            else -> {
            }
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

        // Parar lo que se esté reproduciendo y volver a empezar
        mediaPlayer.reset()
        broadcast(PREPARING)

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
            handler.removeCallbacks(runnable)
            broadcast(PAUSED)
        } else {
            mediaPlayer.start()
            handler.post(runnable)
            broadcast(PLAYING)
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
        stopForeground(true)
        super.onDestroy()
    }

    private fun release() {
        try {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
            broadcast(STOPPED)
        } catch (_: Exception) {
        }
    }

    private fun broadcast(status: AudioStatus? = null) {
        val broadcast = Intent()
        broadcast.action = AUDIO_UPDATES
        broadcast.putExtra(status ?: playingOrPaused())
        if (status != PREPARING) {
            broadcast.putExtra("time", mediaPlayer.currentPosition)
            broadcast.putExtra("duration", mediaPlayer.duration)
        }
        broadcast.putExtra("content", content)
        broadcast.setPackage("com.jackemate.appberdi")
        sendBroadcast(broadcast)
    }

    private fun playingOrPaused() = if (mediaPlayer.isPlaying) PLAYING else PAUSED

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                broadcast(PLAYING)
                handler.postDelayed(this, 1000)
            }
        }
    }

    companion object {
        const val ACTION_SELECT = "com.jackemate.appberdi.audio.action.SELECT"
        const val ACTION_PLAY = "com.jackemate.appberdi.audio.action.PLAY"
        const val ACTION_SEEK = "com.jackemate.appberdi.audio.action.SEEK"
        const val ACTION_SEEK_BY = "com.jackemate.appberdi.audio.action.SEEK_BY"
        const val ACTION_FORCE = "com.jackemate.appberdi.audio.action.FORCE"

        const val EXTRA_OFFSET = "offset"
        const val EXTRA_POSITION = "seek"

        const val AUDIO_UPDATES = "com.jackemate.appberdi.audio.broadcast"
        const val EXTRA_TIME = "time"
        const val EXTRA_STATUS = "status"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_CONTENT = "content"
    }
}