package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMediatecaSiteBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.ui.shared.contents.activities.AudioActivity
import com.jackemate.appberdi.ui.shared.contents.activities.ImageActivity
import com.jackemate.appberdi.ui.shared.contents.activities.VideoActivity
import com.jackemate.appberdi.utils.IntentName
import com.jackemate.appberdi.utils.transparentStatusBar

class MediatecaSiteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatecaSiteBinding
    private val viewModel by viewModels<MediatecaSiteViewModel>()
    private var listContents: List<Content> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaSiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        viewModel.contents.observe(this) {
            listContents = it
            setupChips()
            updateUI()
        }

        val nameSite = intent.getStringExtra("title")!!
        binding.header.text = nameSite
        viewModel.getContents(nameSite)
    }

    private fun setupRecyclerView() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = MediatecaSiteAdapter(
            this,
            emptyList(),
            this::onMultimediaClick
        )
    }

    private fun getAllTags() = listContents.map { it.tag }.toSet().toList()

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
            chip.setOnClickListener { _ -> updateUI() }
        }
    }

    private fun getCheckedTags() = binding.filters.checkedChipIds
        .map { chipTagID -> getAllTags()[chipTagID] }


    private fun updateUI() {
        changeAnimation("Loading")
        val listToShow = if (getCheckedTags().isEmpty()) listContents
        else listContents.filter {
            getCheckedTags().contains(it.tag)
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
            in "Loading" -> binding.animation.setAnimation(R.raw.loading)
            in "Problem" -> binding.animation.setAnimation(R.raw.problem)
            in "Empty" -> binding.animation.setAnimation(R.raw.empty)
            else -> binding.animation.visibility = View.INVISIBLE
        }
        binding.animation.playAnimation()
    }

    private fun onMultimediaClick(multimedia: Content) {
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
            is Content.Video -> {
                val intent = Intent(this, VideoActivity::class.java)
                intent.putExtra(IntentName.TITLE, multimedia.title)
                intent.putExtra(IntentName.DESCRIPTION, multimedia.description)
                intent.putExtra(IntentName.HREF, multimedia.href)
                intent.putExtra(IntentName.DURATION, multimedia.duration)
                startActivity(intent)
            }
            is Content.Text -> {
                val intent = Intent(this, ImageActivity::class.java)
                intent.putExtra(IntentName.TITLE, multimedia.title)
                intent.putExtra(IntentName.DESCRIPTION, multimedia.description)
                intent.putExtra(IntentName.HREF, IntentName.NON_VALUE)
                startActivity(intent)
            }
            is Content.Audio -> startActivity(
                Intent(this, AudioActivity::class.java).apply {
                    putExtra(ARG_CONTENT, multimedia)
                }
            )
        }
    }
}