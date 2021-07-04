package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteAudioFragmentBinding
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.utils.*


class SiteAudioFragment : ContentPageFragment() {

    private val viewModel: SiteViewModel by viewModels()
    private lateinit var binding: SiteAudioFragmentBinding

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
//        binding.siteName.text = content.site

        val c = content
        if (c is Content.Audio) {
            initAudio(c)
        }
    }

    private fun initAudio(content: Content.Audio) {
        Log.i(TAG, "initAudio: $content")
        binding.btnForward.setOnClickListener {
            if (binding.btnPlay.isEnabled) binding.tvCurrentAudio.text = viewModel.forwardAudio()
        }
        binding.btnRewind.setOnClickListener {
            if (binding.btnPlay.isEnabled) binding.tvCurrentAudio.text = viewModel.rewindAudio()
        }

        binding.sbProgress.onSeekBarChanged { progress, fromUser ->
            if (fromUser && binding.btnPlay.isEnabled) {
                viewModel.setMediaPlayerSeekTo(progress)
            }
        }

        viewModel.preStartAudio(content.href) {
            binding.btnPlay.isEnabled = true
            binding.btnRewind.visible(true)
            binding.btnForward.visible(true)
        }

        binding.title.text = content.title
        binding.transcription.text = content.subtitle

        binding.btnShare.setOnClickListener {
            share(content.title, content.href)
        }

        binding.btnPlay.setOnClickListener {
            val playing = viewModel.playPauseAudio()
            binding.btnPlay.setImageResource(
                if (playing) R.drawable.ic_pause
                else R.drawable.ic_play_circle
            )
            binding.tvDurationAudio.text = viewModel.getDurationMedia()
            viewModel.updateProgressAudio(binding.sbProgress, binding.tvCurrentAudio)
        }
    }


//    override fun onBackPressed() {
//        super.onBackPressed()
//        viewModel.pauseAudio()
//    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDisconnect()
    }

}