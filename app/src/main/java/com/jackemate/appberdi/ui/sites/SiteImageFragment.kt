package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteImageFragmentBinding
import com.jackemate.appberdi.domain.entities.Content


class SiteImageFragment : ContentPageFragment() {

    private val viewModel: SiteViewModel by viewModels()
    private lateinit var binding: SiteImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteImageFragmentBinding.inflate(layoutInflater).also { binding = it }
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
        binding.transcription.text = content.description

        Glide.with(requireContext())
            .load(content.href)
            .error(R.drawable.no_image)
            .placeholder(R.drawable.loading)
            .centerCrop()
            .into(binding.img)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDisconnect()
    }

}