package com.jackemate.appberdi.ui.sites

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteAudioFragmentBinding
import com.jackemate.appberdi.databinding.SiteImageFragmentBinding
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.utils.*


class SiteImageFragment : ContentPageFragment() {

    private val viewModel: SiteViewModel by viewModels()
    private lateinit var binding: SiteImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteImageFragmentBinding.inflate(layoutInflater)
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