package com.jackemate.appberdi.ui.sites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivitySiteBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.services.AudioService
import com.jackemate.appberdi.services.TrackingService
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.ui.shared.contents.ARG_ID_SITE
import com.jackemate.appberdi.ui.shared.contents.ContentPageFragment
import com.jackemate.appberdi.ui.shared.contents.fragments.SiteAudioFragment
import com.jackemate.appberdi.ui.shared.contents.fragments.SiteImageFragment
import com.jackemate.appberdi.ui.shared.contents.fragments.SiteSummaryFragment
import com.jackemate.appberdi.ui.shared.contents.fragments.SiteVideoFragment
import com.jackemate.appberdi.ui.shared.dialogs.BasicDialog
import com.jackemate.appberdi.utils.*

class SiteActivity : AppCompatActivity() {

    private val viewModel: SiteViewModel by viewModels()
    private lateinit var binding: ActivitySiteBinding
    private lateinit var site: ContentSite
    private lateinit var idSite: String

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivitySiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idSite = intent.getStringExtra("idSite")!!
        Log.i(TAG, "Site selected: $idSite")
        Firebase.crashlytics.log("Site selected: $idSite")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LEVEL_START) {
            param(FirebaseAnalytics.Param.LEVEL_NAME, idSite)
        }

        binding.loading.visible(true)
        observe(viewModel.getSite(idSite)) { contentSite ->
            if (contentSite == null) return@observe finishWithToast()

            binding.loading.visible(false)
            site = contentSite
            binding.siteName.text = contentSite.name

            Log.i(TAG, "site contentSite size: ${site.contents.size}")
            val tabs = site.contents.map { it.tab }
            binding.steps.setSteps(tabs)

            initStepper()
            initViewPager()
        }

        // Parar el tracking mientras estamos en un sitio
        // Cuando vuelva al mapa el tracker se reiniciará
        stopService(Intent(this, TrackingService::class.java))
    }

    private fun initStepper() {
        binding.btnNext.setOnClickListener {
            nextPage()
        }
        binding.btnBack.setOnClickListener {
            if (isFirst()) {
                createDialogExit()
            } else {
                binding.steps.done(false)
                previousPage()
            }
        }
        binding.steps.setOnStepClickListener {
            setPage(it)
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        createDialogExit()
    }

    private fun createDialogExit() {
        BasicDialog(this)
            .setTitle(getString(R.string.confirmar_salir_title))
            .setSubtitle(getString(R.string.confirmar_salir_subtitle))
            .setButtonText(getString(R.string.salir))
            .setButtonListener { dialog ->
                stopService(Intent(this, AudioService::class.java))
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.LEVEL_END) {
                    param(FirebaseAnalytics.Param.LEVEL_NAME, idSite)
                    param(FirebaseAnalytics.Param.SUCCESS, "false")
                }
                dialog.dismiss()
                finish()
            }.show()
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