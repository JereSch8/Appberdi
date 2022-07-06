package com.jackemate.appberdi.ui.shared.contents.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteVideoFragmentBinding
import com.jackemate.appberdi.utils.IntentName
import com.jackemate.appberdi.utils.share
import com.jackemate.appberdi.utils.transparentStatusBar

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        val binding = SiteVideoFragmentBinding.inflate(layoutInflater)

        val title = intent.getStringExtra(IntentName.TITLE).toString()
        val description = intent.getStringExtra(IntentName.DESCRIPTION).toString()
        val href = intent.getStringExtra(IntentName.HREF).toString()
        val duration = intent.getStringExtra(IntentName.DURATION).toString()

        setContentView(binding.root)

        binding.title.text = title
        binding.description.text = description
        binding.duration.text = getString(R.string.site_video_duration, duration)

        binding.btnShare.setOnClickListener { share(title, href) }

        binding.btnPlay.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(href))
            intent.putExtra("force_fullscreen", true)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.compartir),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

}