package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.jackemate.appberdi.databinding.ActivitySiteBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.ui.sites.contents.SiteAudioFragment
import com.jackemate.appberdi.ui.sites.contents.SiteImageFragment
import com.jackemate.appberdi.ui.sites.contents.SiteSummaryFragment
import com.jackemate.appberdi.ui.sites.contents.SiteVideoFragment
import com.jackemate.appberdi.utils.*

class SiteActivity : FragmentActivity() {

    private val viewModel: SiteViewModel by viewModels()
    private lateinit var binding: ActivitySiteBinding
    private lateinit var site: ContentSite

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivitySiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idSite: String = intent.getStringExtra("idSite")!!

        binding.loading.visible(true)
        observe(viewModel.getSite(idSite)) { contentSite ->
            binding.loading.visible(false)
            site = contentSite
            binding.siteName.text = contentSite.name

            Log.i(TAG, "site contentSite size: ${site.contents.size}")
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

    private fun isLast() = binding.viewPager.currentItem + 1 == binding.viewPager.adapter?.itemCount

    private fun previousPage() {
        val currPos: Int = binding.viewPager.currentItem
        if (currPos != 0) {
            setPage(currPos - 1)
        }
    }

    private fun nextPage() {
        val currPos: Int = binding.viewPager.currentItem
        if (!isLast()) {
            setPage(currPos + 1)
        }
    }

    private fun setPage(position: Int) {
        binding.viewPager.currentItem = position
        binding.steps.go(position, true)
        binding.btnNext.invisible(isLast())
    }

    inner class ContentPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = site.contents.size

        override fun createFragment(position: Int): Fragment {

            val content = site.contents[position]
            val fragment = when (content) {
                is Content.Audio -> SiteAudioFragment()
                is Content.Image -> SiteImageFragment()
                is Content.Video -> SiteVideoFragment()
                is Content.Summary -> SiteSummaryFragment()
                else -> {
                    Log.e(TAG, "createFragment: $content no implementado!")
                    ContentPageFragment()
                }
            }

            fragment.arguments = Bundle().apply {
                putSerializable(ARG_CONTENT, content)
                putString(ARG_ID_SITE, site.idSite)
            }
            return fragment
        }
    }
}