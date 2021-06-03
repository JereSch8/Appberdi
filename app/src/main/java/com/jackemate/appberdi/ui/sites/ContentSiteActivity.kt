package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ContentSiteFragmentBinding
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.utils.*


class ContentSiteActivity : AppCompatActivity() {

    private val viewModel: ContentSiteViewModel by viewModels()
    private var currentStep: Int = 0
    private lateinit var binding: ContentSiteFragmentBinding
    private lateinit var site: ContentSite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentSiteFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idSite: String = intent.getStringExtra("idSite")!!

        observe(viewModel.getSite(idSite)) { contentSite ->
            site = contentSite
            binding.tvNameSite.text = contentSite.name

            Log.i(TAG, "site contents size: ${site.contents.size}")
            val tags = site.contents.map { it.tag }
            binding.steps.setSteps(tags)

            initStepper()
            updateContent()
        }


        // TODO progressbar
        binding.btnPlay.isEnabled = false
        binding.btnRewind.invisible(true)
        binding.btnForward.invisible(true)

    }

    private fun initStepper() {
        binding.btnNext.setOnClickListener {
            if (currentStep < binding.steps.stepCount - 1) {
                currentStep++
                binding.steps.go(currentStep, true)
            }
//                binding.steps.done(true)
            updateContent()
        }
        binding.btnBack.setOnClickListener {
            if (currentStep > 0) {
                currentStep--
            } else {
                finish()
                return@setOnClickListener
            }
            binding.steps.done(false)
            binding.steps.go(currentStep, true)
            updateContent()
        }
        binding.steps.setOnStepClickListener {
            Log.d(TAG, "setOnStepClickListener: $it")
            currentStep = it
            binding.steps.go(currentStep, true)
            updateContent()
        }
    }

    private fun updateContent() {
        // TODO scroll?
        if (site.contents.size <= currentStep) return
        when (val content = site.contents[currentStep]) {
            is Content.Audio -> {
                initAudio(content)
            }
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
        binding.tvTitleAudio.text = content.title

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


    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.pauseAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDisconnect()
    }

}