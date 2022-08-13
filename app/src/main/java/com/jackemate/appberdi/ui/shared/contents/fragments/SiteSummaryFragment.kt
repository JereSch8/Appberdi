package com.jackemate.appberdi.ui.shared.contents.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.activityViewModels
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteSummaryFragmentBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ContentPageFragment
import com.jackemate.appberdi.ui.sites.SiteViewModel
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.invisible
import com.jackemate.appberdi.utils.visible

class SiteSummaryFragment : ContentPageFragment() {

    private lateinit var binding: SiteSummaryFragmentBinding
    private val viewModel: SiteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SiteSummaryFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val c = content
        if (c is Content.Summary) {
            initSummary(c)
        }
    }

    private fun initSummary(content: Content.Summary) {
        binding.btnShare.invisible(true)
        binding.btnShare.setOnClickListener {
//            share(content.title, content.href)
        }

        binding.btnFin.setOnClickListener {
            Log.d(TAG, "fin del sitio: $idSite")
            viewModel.setVisited(idSite!!)
            activity?.finish()
        }

        binding.itemSummaryWords.container.visible(false)
        binding.itemSummaryYears.container.visible(false)
        binding.itemSummaryVecinos.container.visible(false)
        binding.itemSummaryLove.container.visible(false)

        content.words?.let {
            binding.itemSummaryWords.apply {
                container.visible(true)
                title.text = it.title
                subtitle.text = it.subtitle

                ValueAnimator.ofInt(0, it.count ?: 0).apply {
                    duration = 3000
                    addUpdateListener { animation -> wordsNumber.text = animation.animatedValue.toString() }
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
        }

        content.years?.let {
            binding.itemSummaryYears.apply {
                container.visible(true)
                title.text = it.title
                yearStart.text = it.from
                yearEnd.text = it.to
                progressBar.progress = 500

                ObjectAnimator.ofInt(progressBar, "progress", 10000).apply {
                    duration = 4000
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
        }

        content.vecinos?.let {
            binding.itemSummaryVecinos.apply {
                container.visible(true)
                title.text = it.title
                wordsNumber.text = it.count.toString()
                subtitle.text = it.subtitle

                ValueAnimator.ofInt(0, it.count ?: 0).apply {
                    duration = 5000
                    addUpdateListener { animation -> wordsNumber.text = animation.animatedValue.toString() }
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
        }

        content.love?.let {
            binding.itemSummaryLove.apply {
                container.visible(true)
                title.text = it.title
            }
        }

        binding.thanks.text = getString(R.string.gracias_por_tu_visita, viewModel.getName())
    }
}