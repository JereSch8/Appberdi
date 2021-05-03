package com.jackemate.appberdi.ui.sites

import android.graphics.Color
import android.graphics.Insets.add
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jackemate.appberdi.databinding.ContentSiteFragmentBinding
import kotlin.random.Random


class ContentSite : AppCompatActivity() {

    private lateinit var viewModel: ContentSiteViewModel
    private var step : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding  = ContentSiteFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ContentSiteViewModel::class.java)

        binding.steps.state.steps( listOf("Historia", "video", "audio", "anécdota") ).commit()

        binding.btnNext.setOnClickListener { binding.steps.go(++step, true) }
        binding.btnBack.setOnClickListener { binding.steps.go(--step, true) }

        binding.btnPlay.isEnabled = false
        binding.btnPlay.setOnClickListener {
            if(binding.btnPlay.isEnabled) {
                viewModel.startAudio(binding.btnPlay)
                binding.tvDurationAudio.text = viewModel.getDurationMedia()
                viewModel.updateProgressAudio(binding.sbProgress, binding.tvCurrentAudio)
            }
            else
                Toast.makeText(this, "El audio aún no fue cargado.", Toast.LENGTH_SHORT).show()
        }
        binding.btnForward.setOnClickListener { if(binding.btnPlay.isEnabled) binding.tvCurrentAudio.text  = viewModel.forwardAudio() }
        binding.btnRewind.setOnClickListener { if(binding.btnPlay.isEnabled)  binding.tvCurrentAudio.text   = viewModel.rewindAudio() }

        binding.sbProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && binding.btnPlay.isEnabled){
                    viewModel.setMediaPlayerSeekTo(progress)
                }
            }
        })

        val idSite : String = intent.getStringExtra("idSite").toString()

        binding.tvNameSite.text = idSite
        viewModel.printImage(this, "noExist", binding.imgSite)

        viewModel.images.observe(this) {
            if(!it.isNullOrEmpty()){
                val urlImage : String = it[Random.nextInt(it.size)].href
                viewModel.printImage(this, urlImage, binding.imgSite)
            }
        }

        viewModel.audios.observe(this) {
            if(!it.isNullOrEmpty()){
                val urlAudio = it[Random.nextInt(it.size)].href
                viewModel.preStartAudio(urlAudio, binding.btnPlay)
                binding.tvTitleAudio.text = it[Random.nextInt(it.size)].title
            }
        }

        // Actualizamos
        viewModel.getContentImageWhere(idSite)
        viewModel.getContentAudioWhere(idSite)

    }


    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.pauseAudio()
    }



}