package com.jackemate.appberdi.ui.sites

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivitySiteBinding
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.*


class SiteActivity : FragmentActivity() {

    private val viewModel: SiteViewModel by viewModels()
    private var currentStep: Int = 0
    private lateinit var binding: ActivitySiteBinding
    private lateinit var site: ContentSite

    private lateinit var contentAdapter: ContentPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idSite: String = intent.getStringExtra("idSite")!!

        observe(viewModel.getSite(idSite)) { contentSite ->
            site = contentSite
            binding.tvNameSite.text = contentSite.name

            Log.i(TAG, "site contents size: ${site.contents.size}")
            val tags = site.contents.map { it.tag }
            binding.steps.setSteps(tags)

            initStepper()
            initViewPager()
        }
    }

    private fun initStepper() {
        binding.btnNext.setOnClickListener {
            nextPage()
        }
        binding.btnBack.setOnClickListener {
            if (isFirst()) {
                finish()
                return@setOnClickListener
            }
            binding.steps.done(false)
            previousPage()
        }
        binding.steps.setOnStepClickListener {
            setPage(it)
        }
    }

    private fun initViewPager() {
        val pagerAdapter = ContentPagerAdapter(this)
        viewPager = binding.viewPager
        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setPage(position)
            }
        })
    }

    private fun isFirst() = binding.viewPager.currentItem == 0

    private fun previousPage() {
        val currPos: Int = binding.viewPager.currentItem
        if (currPos != 0) {
            setPage(currPos - 1)
        }
    }

    private fun nextPage() {
        val currPos: Int = binding.viewPager.currentItem
        if ((currPos + 1) != binding.viewPager.adapter?.itemCount) {
            setPage(currPos + 1)
        }
    }

    private fun setPage(position: Int) {
        binding.viewPager.currentItem = position
        binding.steps.go(position, true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.pauseAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDisconnect()
    }

    inner class ContentPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = site.contents.size

        override fun createFragment(position: Int): Fragment {

            val content = site.contents[position]
            val fragment = when (content) {
                is Content.Audio -> SiteAudioFragment()
                is Content.Image -> SiteImageFragment()
                else -> ContentPageFragment()
            }

            fragment.arguments = Bundle().apply {
                putSerializable(ARG_CONTENT, content)
                putInt(ARG_INSET, binding.siteHeader.measuredHeight + (actionBar?.height ?: 0))
            }
            return fragment
        }
    }

}