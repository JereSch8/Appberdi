package com.jackemate.appberdi.ui.shared.contents.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jackemate.appberdi.databinding.ActivityContentImagenBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.utils.*

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentImagenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityContentImagenBinding.inflate(layoutInflater)
        binding.back.setOnClickListener { finish() }
        setContentView(binding.root)

        val content = intent.getSerializableExtra(ARG_CONTENT) as Content.Image

        binding.header.text = content.title
        binding.transcription.text = content.description.parseNewLines()

        binding.btnShare.setOnClickListener { share(content.title, content.href) }

        binding.cardView.visible(true)

        Glide.with(this)
            .load(content.href)
            .centerCrop()
            .into(binding.img)

        binding.img.setOnClickListener {
            goToFullscreen()
        }

        Glide.with(this)
            .load(content.href)
            .into(binding.photoView)

        binding.photoView.invisible(true)
        binding.photoView.setOnOutsidePhotoTapListener {
            goToNormal()
        }
    }

    // https://developer.android.com/training/system-ui/immersive
    private fun goToNormal() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        binding.scroll.visible(true)
        binding.photoView.invisible(true)
    }

    private fun goToFullscreen() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        binding.scroll.visible(false)
        binding.photoView.invisible(false)
    }

}