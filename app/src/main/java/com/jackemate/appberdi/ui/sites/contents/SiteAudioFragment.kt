package com.jackemate.appberdi.ui.sites.contents

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteAudioFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.services.AudioService
import com.jackemate.appberdi.services.AudioService.Companion.AUDIO_PLAYING
import com.jackemate.appberdi.services.AudioService.Companion.BROADCAST_UPDATES
import com.jackemate.appberdi.services.AudioService.Companion.BROAD_PROGRESS_UPDATE
import com.jackemate.appberdi.services.AudioService.Companion.EXTRA_OFFSET
import com.jackemate.appberdi.services.AudioService.Companion.EXTRA_SEEK
import com.jackemate.appberdi.ui.sites.ContentPageFragment
import com.jackemate.appberdi.utils.*


class SiteAudioFragment : ContentPageFragment() {
    private lateinit var binding: SiteAudioFragmentBinding
    private var currentPreview: String? = null

    private var audioService: AudioService? = null
    private val receiver = AudioBroadcastReceiver()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioService.TourServiceBinder
            audioService = binder.service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            audioService = null
        }
    }

    inner class AudioBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Action: ${intent.action}}")

            if (intent.action == BROAD_PROGRESS_UPDATE) {
                val time = intent.getIntExtra("time", 0)
                val status = intent.getIntExtra("status", 0)
                val duration = intent.getIntExtra("duration", 0)
                updateUI(status, time, duration)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Intent(requireActivity(), AudioService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        requireActivity().registerReceiver(receiver,
            IntentFilter(BROADCAST_UPDATES).apply {
                addAction(BROAD_PROGRESS_UPDATE)
            }
        )

        ContextCompat.startForegroundService(
            requireActivity(),
            Intent(requireActivity(), AudioService::class.java).apply {
                action = AudioService.ACTION_FORCE
            }
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unbindService(connection)
        audioService = null
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteAudioFragmentBinding.inflate(layoutInflater)

        // Setear el alto del playerContainer del mismo alto
        // que el scrollView, menos un pequeño offset (el alto del title)
        binding.scrollView.post {
            val layout = binding.playerContainer.layoutParams
            layout.height = binding.scrollView.height - binding.title.height * 2
            binding.playerContainer.layoutParams = layout
            Log.w(TAG, "set height: ${layout.height}")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val c = content
        if (c is Content.Audio) {
            initAudio(c)
        }
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
            playPauseAudio()
        }

        binding.btnForward.setOnClickListener {
            seekBy(5000)
        }
        binding.btnRewind.setOnClickListener {
            seekBy(-5000)
        }

        binding.sbProgress.onSeekBarChanged { progress, fromUser ->
            if (fromUser) {
                val intent = Intent(requireActivity(), AudioService::class.java)
                intent.action = AudioService.ACTION_SEEK
                intent.putExtra(EXTRA_SEEK, progress)
                ContextCompat.startForegroundService(requireActivity(), intent)
            }
        }

        // Prefech la primera imagen
        updatePreview("00:00")
    }

    private fun onMediaPlayerPrepared() {
        binding.btnPlay.isEnabled = true
        binding.btnRewind.visible(true)
        binding.btnForward.visible(true)
        binding.sbProgress.visible(true)
    }

    fun updateUI(status: Int, time: Int, duration: Int) {
        binding.sbProgress.max = duration
        binding.sbProgress.progress = time
        binding.tvCurrentAudio.text = time.toTimeString()
        binding.tvDurationAudio.text = duration.toTimeString()

        binding.btnPlay.setImageResource(
            if (status == AUDIO_PLAYING) R.drawable.ic_pause
            else R.drawable.ic_play_circle
        )
        updatePreview(time.toTimeString())
    }

    private fun updatePreview(time: String) {
        val c = content as Content.Audio
        c.preview.keys
            .sorted()
            /*
             * Estoy comparando strings con la forma "xx:xx"
             * Cuestionable, pero funciona razonablemente bien
             * ya que están ordenados cronológicamente.
             *
             * Quiero la primer key que apenas es más grande que
             * el tiempo actual.
             */
            .findLast { it <= time }
            /*
             * Como tengo que llamar a Glide
             * no quiero ser muy pesado, así que sólo voy a hacerlo
             * si cambia la url de imagen.
             */
            ?.takeIf { c.preview[it] != c.preview[currentPreview ?: ""] }
            ?.let {
                Log.d(TAG, "updateUI: change preview: $it")
                currentPreview = it
                Glide.with(requireContext())
                    .load(c.preview[it])
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .into(binding.img)
            }
    }

    private fun playPauseAudio() {
        val intent = Intent(requireActivity(), AudioService::class.java)
        intent.action = AudioService.ACTION_PLAY
        ContextCompat.startForegroundService(requireActivity(), intent)
    }

    private fun seekBy(change: Int) {
        if (!binding.btnPlay.isEnabled) return // MediaPlayer not ready

        val intent = Intent(requireActivity(), AudioService::class.java)
        intent.action = AudioService.ACTION_SEEK
        intent.putExtra(EXTRA_OFFSET, change)
        ContextCompat.startForegroundService(requireActivity(), intent)
    }

}