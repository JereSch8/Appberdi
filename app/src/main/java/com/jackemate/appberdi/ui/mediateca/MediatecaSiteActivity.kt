package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMediatecaSiteBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.ui.shared.contents.activities.AudioActivity
import com.jackemate.appberdi.ui.shared.contents.activities.ImageActivity
import com.jackemate.appberdi.ui.shared.contents.activities.TextActivity
import com.jackemate.appberdi.ui.shared.contents.activities.VideoActivity
import com.jackemate.appberdi.utils.IntentName
import com.jackemate.appberdi.utils.finishWithToast
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.transparentStatusBar

class MediatecaSiteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatecaSiteBinding
    private val viewModel by viewModels<MediatecaSiteViewModel>()
    private var listContents: List<Content> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaSiteBinding.inflate(layoutInflater)
        binding.header.back.setOnClickListener { finish() }
        setContentView(binding.root)

        setupRecyclerView()

        val nameSite = intent.getStringExtra("title")!!
        binding.header.title.text = nameSite
        observe(viewModel.getContents(nameSite)) { list ->
            if (list == null) return@observe finishWithToast()
            listContents = list
            setupChips()
            updateUI()
        }

    }

    private fun setupRecyclerView() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = MediatecaSiteAdapter(
            this,
            emptyList(),
            this::onMultimediaClick
        )
    }

    private fun getAllTags() = listContents.map { it.tab }.toSet().toList()

    private fun setupChips() {
        // Si se restaura el estado de la Activity
        // No quiero duplicar los chips
        if (binding.filters.childCount > 0) return

        for ((index, site) in getAllTags().withIndex()) {
            val chip = Chip(this)
            chip.text = site
            chip.id = index
            chip.isClickable = true
            chip.isCheckable = true
            binding.filters.addView(chip)
            chip.setOnClickListener { updateUI() }
        }
    }

    private fun getCheckedTags() = binding.filters.checkedChipIds
        .map { chipTagID -> getAllTags()[chipTagID] }


    private fun updateUI() {
        changeAnimation("Loading")
        val listToShow = if (getCheckedTags().isEmpty()) listContents
        else listContents.filter {
            getCheckedTags().contains(it.tab)
        }

        if (listToShow.isNullOrEmpty())
            changeAnimation("Empty")
        else
            changeAnimation("Invisible")

        val adapter = binding.recycler.adapter as MediatecaSiteAdapter
        adapter.updateItems(listToShow.sortedBy { it.title })
    }

    private fun changeAnimation(change: String) {
        binding.animation.visibility = View.VISIBLE
        binding.animation.setAnimation(R.raw.loading)
        when (change) {
            "Loading" -> binding.animation.setAnimation(R.raw.loading)
            "Empty" -> binding.animation.setAnimation(R.raw.empty)
            else -> binding.animation.visibility = View.INVISIBLE
        }
        binding.animation.playAnimation()
    }

    private fun onMultimediaClick(multimedia: Content) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, multimedia.type)
            param(FirebaseAnalytics.Param.ITEM_ID, multimedia.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, multimedia.title)
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, multimedia.site)
        }
        when (multimedia) {
            is Content.Image -> startActivity(
                Intent(this, ImageActivity::class.java).apply {
                    putExtra(ARG_CONTENT, multimedia)
                }
            )
            is Content.Gif -> Toast.makeText(
                baseContext,
                "Es un gif ${multimedia.site}",
                Toast.LENGTH_LONG
            ).show()
            is Content.Video -> startActivity(
                Intent(this, VideoActivity::class.java).apply {
                    putExtra(IntentName.TITLE, multimedia.title)
                    putExtra(IntentName.DESCRIPTION, multimedia.description)
                    putExtra(IntentName.HREF, multimedia.href)
                    putExtra(IntentName.DURATION, multimedia.duration)
                }
            )
            is Content.Text -> startActivity(
                Intent(this, TextActivity::class.java).apply {
                    putExtra(ARG_CONTENT, multimedia)
                }
            )
            is Content.Audio -> startActivity(
                Intent(this, AudioActivity::class.java).apply {
                    putExtra(ARG_CONTENT, multimedia)
                }
            )
            else -> Toast.makeText(
                baseContext,
                "No deberías ver esto :O",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}