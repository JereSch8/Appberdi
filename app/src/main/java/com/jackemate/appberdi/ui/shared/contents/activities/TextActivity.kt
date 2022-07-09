package com.jackemate.appberdi.ui.shared.contents.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jackemate.appberdi.databinding.ActivityContentImagenBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.utils.invisible
import com.jackemate.appberdi.utils.share
import com.jackemate.appberdi.utils.transparentStatusBar
import com.jackemate.appberdi.utils.visible

class TextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentImagenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityContentImagenBinding.inflate(layoutInflater)
        binding.back.setOnClickListener { finish() }
        setContentView(binding.root)

        val content = intent.getSerializableExtra(ARG_CONTENT) as Content.Text

        binding.header.text = content.title
        binding.transcription.text = content.description
        binding.btnShare.setOnClickListener {
            share(content.title, content.description)
        }
    }
}