package com.jackemate.appberdi.ui.view_contents

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteAudioFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.*
import java.util.concurrent.TimeUnit

class AudioActivity : AppCompatActivity() {

    private lateinit var binding : SiteAudioFragmentBinding
    private lateinit var content : Content.Audio
    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = SiteAudioFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        initContent()
        initAudio()
    }

    private fun setup(){
        binding.btnPlay.isEnabled = false
        binding.btnRewind.invisible(true)
        binding.btnForward.invisible(true)
        binding.sbProgress.invisible(true)
    }

    private fun initContent() {
        val subtitle = intent.getStringExtra(IntentName.SUBTITLE).toString()
        val href = intent.getStringExtra(IntentName.HREF).toString()

        content =  Content.Audio(href,subtitle)
    }

    private fun initAudio() {
        Log.i(TAG, "initAudio: $content")
        val title = intent.getStringExtra(IntentName.TITLE).toString()
        binding.title.text = title
        binding.transcription.text = content.subtitle

        binding.btnShare.setOnClickListener {
            share(title, content.href)
        }

        mediaPlayer.apply {
            setDataSource(content.href)
            prepareAsync()
            // Evitar que el celu entre en modo hibernación mientras reproduce audio
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setOnPreparedListener {
                onMediaPlayerPrepared()
            }
            setOnSeekCompleteListener {
                Log.d(TAG, "setOnSeekCompleteListener")
                updateUI()
            }
            setOnCompletionListener {
                Log.d(TAG, "setOnCompletionListener")
                updateUI()
            }
        }

        binding.btnPlay.setImageResource(R.drawable.ic_play_circle)
        binding.btnPlay.setOnClickListener {
            playPauseAudio()
        }

        binding.btnForward.setOnClickListener {
            seekBy(5000)
        }
        binding.btnRewind.setOnClickListener {
            seekBy(-5000)
        }

        binding.sbProgress.onSeekBarChanged { progress, fromUser ->
            if (fromUser)
                mediaPlayer.seekTo(progress)
        }

    }

    private fun onMediaPlayerPrepared() {
        binding.btnPlay.isEnabled = true
        binding.btnRewind.visible(true)
        binding.btnForward.visible(true)
        binding.sbProgress.visible(true)

        binding.tvCurrentAudio.text = durationToString(mediaPlayer.currentPosition)
        val duration = mediaPlayer.duration
        binding.sbProgress.max = duration
        binding.tvDurationAudio.text = durationToString(duration)
        Log.d(TAG, "setOnPreparedListener: $duration")
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            updateUI()
            if (mediaPlayer.isPlaying) {
                handler.postDelayed(this, 1000)
            } else {
                Log.d("SiteAudioFragment", "runnable: stop")
                handler.removeCallbacks(this)
            }
        }
    }

    private fun updateUI() {
        binding.sbProgress.progress = mediaPlayer.currentPosition
        val time = durationToString(mediaPlayer.currentPosition)
        binding.tvCurrentAudio.text = time
        binding.btnPlay.setImageResource(
            if (mediaPlayer.isPlaying) R.drawable.ic_pause
            else R.drawable.ic_play_circle
        )
    }


    private fun playPauseAudio() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
            handler.post(runnable)
        }
    }

    private fun seekBy(change: Int) {
        if (!binding.btnPlay.isEnabled) return // MediaPlayer not ready

        val position = mediaPlayer.currentPosition + change
        mediaPlayer.seekTo(position.coerceIn(0, mediaPlayer.duration))
    }

    private fun durationToString(duration: Int) = String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
        TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) % 60
    )

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}