package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMediatecaBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.view_contents.AudioActivity
import com.jackemate.appberdi.ui.view_contents.ImageActivity
import com.jackemate.appberdi.ui.view_contents.VideoActivity
import com.jackemate.appberdi.utils.IntentName
import com.jackemate.appberdi.utils.transparentStatusBar

class Mediateca : AppCompatActivity() {

    private lateinit var binding: ActivityMediatecaBinding
    private val viewModel by viewModels<MediatecaViewModel>()
    private var listContents: List<Content> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()

        viewModel.contents.observe(this) {
            listContents = it
            setupChips()
            updateUI()
        }

        val nameSite = intent.getStringExtra("title").toString()
        viewModel.getContents(nameSite)
    }

    private fun setupRecyclerView() {
        binding.contentRecycler.recycler.layoutManager = LinearLayoutManager(this)
        binding.contentRecycler.recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.contentRecycler.recycler.adapter = MediatecaAdapter(
            this,
            emptyList(),
            this::onMultimediaClick
        )
    }

    private fun getAllTags() = listContents.map { it.tag }.toSet().toList()

    private fun setupChips() {
        for ((index, site) in getAllTags().withIndex()) {
            val chip = Chip(binding.cgTag.context)
            chip.text = site
            chip.id = index
            chip.isClickable = true
            chip.isCheckable = true
            binding.cgTag.addView(chip)
            chip.setOnCheckedChangeListener { _, _ -> updateUI() }
        }
    }

    private fun getCheckedTags() = binding.cgTag.checkedChipIds
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

        val adapter = binding.contentRecycler.recycler.adapter as MediatecaAdapter
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
            is Content.Image -> {
                val intent = Intent(this, ImageActivity::class.java)
                intent.putExtra(IntentName.TITLE, multimedia.title)
                intent.putExtra(IntentName.DESCRIPTION, multimedia.description)
                intent.putExtra(IntentName.HREF, multimedia.href)
                startActivity(intent)
            }
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
            is Content.Audio -> {
                val intent = Intent(this, AudioActivity::class.java)
                intent.putExtra(IntentName.TITLE, multimedia.title)
                intent.putExtra(IntentName.SUBTITLE, multimedia.subtitle)
                intent.putExtra(IntentName.HREF, multimedia.href)
                startActivity(intent)
            }
            else -> {
            }
        }
    }
}