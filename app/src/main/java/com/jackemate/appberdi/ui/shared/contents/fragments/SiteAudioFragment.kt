package com.jackemate.appberdi.ui.shared.contents.fragments

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
import com.jackemate.appberdi.entities.AudioStatus
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.services.AudioService
import com.jackemate.appberdi.services.AudioService.Companion.AUDIO_UPDATES
import com.jackemate.appberdi.ui.shared.contents.ContentPageFragment
import com.jackemate.appberdi.utils.*
import kotlin.math.abs


class SiteAudioFragment : ContentPageFragment() {
    private lateinit var binding: SiteAudioFragmentBinding
    private var currentPreview: String? = null

    private var audioService: AudioService? = null
    private val receiver = AudioBroadcastReceiver()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioService.TourServiceBinder
            audioService = binder.service
            binder.service.actionSelect(content as Content.Audio)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            audioService = null
        }
    }

    inner class AudioBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.v(TAG, intent.pretty())

            if (intent.action == AUDIO_UPDATES) {
                val time = intent.getIntExtra("time", 0)
                val status = intent.getEnumExtra<AudioStatus>()
                val duration = intent.getIntExtra("duration", 0)
                updateUI(status, time, duration)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteAudioFragmentBinding.inflate(layoutInflater)

        // Setear el alto del playerContainer del mismo alto
        // que el scrollView, menos un pequeño offset
        binding.scrollView.post {
            val scrollHeight = binding.scrollView.height

            binding.playerContainer.apply {
                val params = layoutParams
                params.height = scrollHeight - binding.descriptionTitle.height - dp(8 + 16)
                layoutParams = params
            }

            binding.transcriptionCv.apply {
                val params = binding.transcriptionCv.layoutParams
                params.height = scrollHeight -
                        getDistanceBetweenViews(binding.sbProgress, binding.transcription) -
                        dp(8 + 16)
                layoutParams = params
            }
        }

        return binding.root
    }

    // https://stackoverflow.com/a/50774474/11385657
    private fun getDistanceBetweenViews(firstView: View, secondView: View): Int {
        val firstPosition = IntArray(2)
        val secondPosition = IntArray(2)
        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        firstView.getLocationOnScreen(firstPosition)
        secondView.getLocationOnScreen(secondPosition)
        val b = firstView.measuredHeight + firstPosition[1]
        val t = secondPosition[1]
        return abs(b - t)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(receiver, IntentFilter(AUDIO_UPDATES))

        Intent(requireActivity(), AudioService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        ContextCompat.startForegroundService(
            requireActivity(),
            Intent(requireActivity(), AudioService::class.java)
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unbindService(connection)
        requireActivity().unregisterReceiver(receiver)
        audioService = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAudio(content as Content.Audio)
    }

    private fun initAudio(content: Content.Audio) {
        Log.i(TAG, "initAudio: $content")

        binding.title.text = content.title
        binding.transcription.text = content.subtitle.parseNewLines()

        binding.btnShare.setOnClickListener {
            requireContext().share(content.title, content.href)
        }

        binding.btnPlay.setIconResource(R.drawable.ic_play_circle)
        binding.btnPlay.setOnClickListener {
            audioService?.actionPlay()
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

        // Prefech la primera imagen
        updatePreview("00:00")
    }

    private fun seekBy(change: Int) {
        if (!binding.btnPlay.isEnabled) return // MediaPlayer not ready
        audioService?.actionSeekBy(change)
    }

    fun updateUI(status: AudioStatus, time: Int, duration: Int) {
        binding.sbProgress.invisible(status == AudioStatus.PREPARING)
        binding.loading.invisible(status != AudioStatus.PREPARING)

        binding.btnPlay.isEnabled = status != AudioStatus.PREPARING
        binding.btnRewind.isEnabled = status != AudioStatus.PREPARING
        binding.btnForward.isEnabled = status != AudioStatus.PREPARING

        binding.sbProgress.max = duration
        binding.sbProgress.progress = time
        binding.tvCurrentAudio.text = time.toTimeString()
        binding.tvDurationAudio.text = duration.toTimeString()

        binding.btnPlay.setIconResource(
            if (status == AudioStatus.PLAYING) R.drawable.ic_pause
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
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(binding.img)
            }
    }

}