package com.jackemate.appberdi.ui.sites.contents

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteAudioFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.sites.ContentPageFragment
import com.jackemate.appberdi.utils.*
import java.util.concurrent.TimeUnit


class SiteAudioFragment : ContentPageFragment() {
    private lateinit var binding: SiteAudioFragmentBinding
    private val mediaPlayer = MediaPlayer()
    private var currentPreview: String? = null

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

        binding.btnPlay.isEnabled = false
        binding.btnRewind.invisible(true)
        binding.btnForward.invisible(true)
        binding.sbProgress.invisible(true)

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

        mediaPlayer.apply {
            setDataSource(content.href)
            prepareAsync()
            // Evitar que el celu entre en modo hibernación mientras reproduce audio
            setWakeMode(requireContext().applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
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
            if (fromUser) {
                mediaPlayer.seekTo(progress)
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
//                Log.d(
//                    "SiteAudioFragment",
//                    "runnable: currentPosition: ${mediaPlayer.currentPosition}"
//                )
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
        updatePreview(time)
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