package com.jackemate.appberdi.ui.sites

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteVideoFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.share


class SiteVideoFragment : ContentPageFragment() {

    private lateinit var binding: SiteVideoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteVideoFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val c = content
        if (c is Content.Video) {
            initVideo(c)
        }
    }

    private fun initVideo(content: Content.Video) {
        binding.title.text = content.title
        binding.description.text = content.description
        binding.duration.text = getString(R.string.site_video_duration, content.duration)

        binding.btnShare.setOnClickListener {
            share(content.title, content.href)
        }

        binding.btnPlay.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(content.href))
            intent.putExtra("force_fullscreen", true)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "No pude abrir el video, ¿y si le compartís el link a alguien y lo ves en compañia?",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }
}