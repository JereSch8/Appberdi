package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
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

class Mediateca : AppCompatActivity(), MediatecaAdapter.OnMultimediaClickListener {

    private lateinit var binding : ActivityMediatecaBinding
    private val viewModel by viewModels<MediatecaViewModel>()
    private lateinit var nameSite : String
    private var listImage : List<Content.Image> = emptyList()
    private var listVideo : List<Content.Video> = emptyList()
    private var listGif : List<Content.Gif> = emptyList()
    private var listText : List<Content.Text> = emptyList()
    private var listAudio : List<Content.Audio> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        nameSite = intent.getStringExtra("title").toString()
        viewModel.getContents(nameSite)

        setupChips()

        viewModel.images.observe(this){ listImage = it }
        viewModel.gifs.observe(this)  { listGif = it }
        viewModel.audios.observe(this){ listAudio = it }
        viewModel.videos.observe(this){ listVideo = it }
        viewModel.texts.observe(this) { listText = it }

        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        binding.contentRecycler.recycler.layoutManager = LinearLayoutManager(this)
        binding.contentRecycler.recycler .addItemDecoration(
            DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        )
    }

    private fun setupChips(){
        for ((index, site) in viewModel.tags.withIndex()) {
            val chip = Chip(binding.cgTag.context)
            chip.text= site
            chip.id = index
            chip.isClickable = true
            chip.isCheckable = true
            binding.cgTag.addView(chip)
            chip.setOnCheckedChangeListener { _, _ -> searchByTag() }
        }
    }

    private fun getTagName() : List<String>{
        var listTags : List<String> = emptyList()

        binding.cgTag.checkedChipIds.forEach { chipTagID ->
            listTags += viewModel.tags[chipTagID]
        }

        return listTags
    }

    private fun searchByTag() {
        changeAnimation("Loading")
        var listContents : List<Any> = emptyList()
        getTagName().forEach { tag ->
            when(tag){
                in "Texto" -> listContents += listText
                in "Audio" -> listContents += listAudio
                in "Video" -> listContents += listVideo
                in "Gif" -> listContents += listGif
                in "Imagen" -> listContents += listImage
                else -> changeAnimation("Problem")
            }
        }

        if (listContents.isNullOrEmpty())
            changeAnimation("Empty")
        else
            changeAnimation("Invisible")

        binding.contentRecycler.recycler.adapter = MediatecaAdapter(
            this,
            listContents,
            this
        )
    }

    private fun changeAnimation(change : String){
        binding.animation.visibility = View.VISIBLE
        binding.animation.setAnimation(R.raw.loading)
        when(change){
            in "Loading" -> binding.animation.setAnimation(R.raw.loading)
            in "Problem" -> binding.animation.setAnimation(R.raw.problem)
            in "Empty" -> binding.animation.setAnimation(R.raw.empty)
            else -> binding.animation.visibility = View.INVISIBLE
        }
        binding.animation.playAnimation()
    }

    override fun onMultimediaClick(multimedia: Any) {
        when(multimedia){
            is Content.Image -> {
                val intent = Intent(this, ImageActivity::class.java)
                intent.putExtra(IntentName.TITLE, multimedia.title)
                intent.putExtra(IntentName.DESCRIPTION, multimedia.description)
                intent.putExtra(IntentName.HREF, multimedia.href)
                startActivity(intent)
            }
            is Content.Gif -> Toast.makeText(baseContext, "Es un gif ${multimedia.site}", Toast.LENGTH_LONG ).show()
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
        }
    }
}