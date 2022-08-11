package com.jackemate.appberdi.ui.shared.contents.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteImageFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ContentPageFragment
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.parseNewLines
import com.jackemate.appberdi.utils.share

class SiteImageFragment : ContentPageFragment() {

    private lateinit var binding: SiteImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteImageFragmentBinding.inflate(layoutInflater)

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

        val c = content
        if (c is Content.Image) {
            initImage(c)
        }
    }

    private fun initImage(content: Content.Image) {
        binding.title.text = content.title
        binding.transcription.text = content.description.parseNewLines()

        binding.btnShare.setOnClickListener {
            requireContext().share(content.title, content.href)
        }

        Glide.with(requireContext())
            .load(content.href)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.img)
    }
}