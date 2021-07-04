package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackemate.appberdi.databinding.SiteSummaryFragmentBinding
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.utils.share


class SiteSummaryFragment : ContentPageFragment() {

    private lateinit var binding: SiteSummaryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteSummaryFragmentBinding.inflate(layoutInflater)
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

        binding.btnShare.setOnClickListener {
            share(content.title, content.href)
        }
    }
}