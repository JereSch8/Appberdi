package com.jackemate.appberdi.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.databinding.ActivityAboutBinding
import com.jackemate.appberdi.utils.open
import com.jackemate.appberdi.utils.transparentStatusBar

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.albiInsta.setOnClickListener {
            open("https://www.instagram.com/appberdi/")
        }
        binding.albiGithub.setOnClickListener {
            open("https://github.com/JereSch8/AppBerdi")
        }
    }
}