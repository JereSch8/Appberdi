package com.jackemate.appberdi.ui.view_contents

import android.content.*
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteAudioFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.services.AudioService
import com.jackemate.appberdi.ui.sites.ARG_CONTENT
import com.jackemate.appberdi.utils.*

class AudioActivity : AppCompatActivity() {

    private lateinit var binding: SiteAudioFragmentBinding
    private lateinit var content: Content.Audio

    private var audioService: AudioService? = null
    private val receiver = AudioBroadcastReceiver()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioService.TourServiceBinder
            audioService = binder.service
            binder.service.actionSelect(content)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            audioService = null
        }
    }

    inner class AudioBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.v(TAG, "Action: ${intent.action}}")

            if (intent.action == AudioService.BROAD_PROGRESS_UPDATE) {
                val time = intent.getIntExtra("time", 0)
                val status = intent.getIntExtra("status", 0)
                val duration = intent.getIntExtra("duration", 0)
                updateUI(status, time, duration)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = SiteAudioFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        content = intent?.getSerializableExtra(ARG_CONTENT) as Content.Audio?
            ?: throw Exception("No argument ARG_CONTENT")

        binding.scrollView.fitsSystemWindows = true

        // Setear el alto del playerContainer del mismo alto
        // que el scrollView, menos un pequeño offset (el alto del title)
        // El último es un número que se multiplica es mágico xD just works
        binding.scrollView.post {
            val layout = binding.playerContainer.layoutParams
            layout.height = binding.scrollView.height - binding.title.height * 5
            binding.playerContainer.layoutParams = layout
            Log.w(TAG, "set height: ${layout.height}")
        }

        initAudio(content)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver,
            IntentFilter(AudioService.BROADCAST_UPDATES).apply {
                addAction(AudioService.BROAD_PROGRESS_UPDATE)
            }
        )

        Intent(this, AudioService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }

        ContextCompat.startForegroundService(
            this,
            Intent(this, AudioService::class.java)
        )
    }

    override fun onPause() {
        super.onPause()
        unbindService(connection)
        audioService = null
        unregisterReceiver(receiver)
    }

    private fun initAudio(content: Content.Audio) {
        Log.i(TAG, "initAudio: $content")

        binding.title.text = content.title
        binding.transcription.text = content.subtitle

        binding.btnShare.setOnClickListener {
            share(content.title, content.href)
        }

        binding.btnPlay.setImageResource(R.drawable.ic_play_circle)
        binding.btnPlay.setOnClickListener {
            audioService?.actionPlay()
        }

        fun seekBy(change: Int) {
            if (!binding.btnPlay.isEnabled) return // MediaPlayer not ready
            audioService?.actionSeekBy(change)
        }

        binding.btnForward.setOnClickListener {
            seekBy(5000)
        }
        binding.btnRewind.setOnClickListener {
            seekBy(-5000)
        }

        binding.sbProgress.onSeekBarChanged { progress, fromUser ->
            if (fromUser) {
                audioService?.actionSeek(progress)
            }
        }
    }

    fun updateUI(status: Int, time: Int, duration: Int) {
        binding.sbProgress.invisible(status == AudioService.AUDIO_PREPARING)
        binding.loading.invisible(status != AudioService.AUDIO_PREPARING)

        binding.sbProgress.max = duration
        binding.sbProgress.progress = time
        binding.tvCurrentAudio.text = time.toTimeString()
        binding.tvDurationAudio.text = duration.toTimeString()

        binding.btnPlay.setImageResource(
            if (status == AudioService.AUDIO_PLAYING) R.drawable.ic_pause
            else R.drawable.ic_play_circle
        )
    }

}