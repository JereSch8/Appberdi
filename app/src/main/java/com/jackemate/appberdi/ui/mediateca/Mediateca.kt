package com.jackemate.appberdi.ui.mediateca

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
import com.jackemate.appberdi.domain.entities.Content

class Mediateca : AppCompatActivity(), MediatecaAdapter.OnMultimediaClickListener {

    private lateinit var binding : ActivityMediatecaBinding
    private val viewModel by viewModels<MediatecaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupChips()

        viewModel.images.observe(this){
            binding.contentRecycler.recycler.adapter = MediatecaAdapter(this, it,this)
            updateAnim(!it.isNullOrEmpty())
            binding.toolbarLayout.title = "${it.size} archivos encontrados."
        }
        viewModel.gifs.observe(this){
            binding.contentRecycler.recycler.adapter = MediatecaAdapter(this, it,this)
            updateAnim(!it.isNullOrEmpty())
            binding.toolbarLayout.title = "${it.size} archivos encontrados."
        }
        viewModel.audios.observe(this){
            binding.contentRecycler.recycler.adapter = MediatecaAdapter(this, it,this)
            updateAnim(!it.isNullOrEmpty())
            binding.toolbarLayout.title = "${it.size} archivos encontrados."
        }
        viewModel.videos.observe(this){
            binding.contentRecycler.recycler.adapter = MediatecaAdapter(this, it,this)
            updateAnim(!it.isNullOrEmpty())
            binding.toolbarLayout.title = "${it.size} archivos encontrados."
        }
        viewModel.texts.observe(this){
            binding.contentRecycler.recycler.adapter = MediatecaAdapter(this, it,this)
            updateAnim(!it.isNullOrEmpty())
            binding.toolbarLayout.title = "${it.size} archivos encontrados."
        }

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
        for ((index, site) in viewModel.nameSites.withIndex()) {
            val chip = Chip(binding.cgSite.context)
            chip.text= site
            chip.id = index
            // necessary to get single selection working
            chip.isClickable = true
            chip.isCheckable = true
            binding.cgSite.addView(chip)
        }

        for ((index, site) in viewModel.tags.withIndex()) {
            val chip = Chip(binding.cgTag.context)
            chip.text= site
            chip.id = index
            // necessary to get single selection working
            chip.isClickable = true
            chip.isCheckable = true
            binding.cgTag.addView(chip)
        }

        chipsChangeListener()
    }

    private fun chipsChangeListener(){
        binding.cgSite.setOnCheckedChangeListener { chipGroup, i -> actionSearch() }
        binding.cgTag.setOnCheckedChangeListener { chipGroup, i -> actionSearch() }
    }

    private fun actionSearch(){
        val chipSiteID = binding.cgSite.checkedChipId
        val chipTagID = binding.cgTag.checkedChipId
        if(chipSiteID != -1){
            if(chipTagID != -1) {
                changeAnimation("Loading")
                searchByTag(viewModel.tags[chipTagID], viewModel.nameSites[chipSiteID])
            }
            else{
                Toast.makeText(baseContext, "Debe Seleccionar un tag.",Toast.LENGTH_LONG).show()
                changeAnimation("Invisible")
            }
        }
        else{
            Toast.makeText(baseContext, "Debe Seleccionar un sitio.",Toast.LENGTH_LONG).show()
            changeAnimation("Invisible")
        }

    }

    private fun searchByTag(tag : String, nameSite : String) {
        when(tag){
            in "Texto" -> viewModel.getText(nameSite)
            in "Audio" -> viewModel.getAudios(nameSite)
            in "Video" -> viewModel.getVideos(nameSite)
            in "Gif" -> viewModel.getGifs(nameSite)
            in "Imagen" -> viewModel.getImages(nameSite)
            else -> changeAnimation("Problem")
        }
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

    private fun updateAnim(isOk : Boolean){
        if(isOk)
            changeAnimation("Invisible")
        else
            changeAnimation("Empty")
    }

    override fun onMultimediaClick(multimedia: Any) {
        when(multimedia){
            is Content.Image -> Toast.makeText(baseContext, "Es una imagen ${multimedia.site}", Toast.LENGTH_LONG ).show()
            is Content.Gif -> Toast.makeText(baseContext, "Es un gif ${multimedia.site}", Toast.LENGTH_LONG ).show()
            is Content.Video -> Toast.makeText(baseContext, "Es un video ${multimedia.title}", Toast.LENGTH_LONG ).show()
            is Content.Text -> Toast.makeText(baseContext, "Es un texto ${multimedia.title}", Toast.LENGTH_LONG ).show()
            is Content.Audio -> Toast.makeText(baseContext, "Es un audio ${multimedia.title}", Toast.LENGTH_LONG ).show()
        }
    }
}