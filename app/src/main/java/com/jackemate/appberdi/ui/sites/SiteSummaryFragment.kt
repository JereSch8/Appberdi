package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jackemate.appberdi.databinding.SiteSummaryFragmentBinding
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.utils.TAG

class SiteSummaryFragment : ContentPageFragment() {

    private lateinit var binding: SiteSummaryFragmentBinding
    private val viewModel: SiteViewModel by viewModels()

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
        if (c is Content.Summary) {
            initSummary(c)
        }
    }

    private fun initSummary(content: Content.Summary) {
        binding.title.text = content.title
        binding.description.text = content.description

        binding.btnShare.setOnClickListener {
//            share(content.title, content.href)
        }

        binding.btnFin.setOnClickListener {
            Log.d(TAG, "fin del sitio: $idSite")
            viewModel.setVisited(idSite)
            activity?.finish()
        }
    }
}