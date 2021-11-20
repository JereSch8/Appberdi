package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jackemate.appberdi.databinding.ActivityMediatecaStartBinding
import com.jackemate.appberdi.entities.ContentMediateca
import com.jackemate.appberdi.utils.transparentStatusBar

class MediatecaStart : AppCompatActivity() {

    private lateinit var binding: ActivityMediatecaStartBinding
    private val viewModel: MediatecaStartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.contents.layoutManager = GridLayoutManager(this, 2)

        viewModel.contents.observe(this) { listContents ->
            binding.contents.adapter = MediatecaStartAdapter(listContents, this::onSelected)
        }
        viewModel.getContents()
    }

    private fun onSelected(item: ContentMediateca) {
        val intent = Intent(this, MediatecaSiteActivity::class.java)
        intent.putExtra("title", item.title)
        startActivity(intent)
    }

}