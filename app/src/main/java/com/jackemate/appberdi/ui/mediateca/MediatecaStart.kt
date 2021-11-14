package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.databinding.ActivityMediatecaStartBinding
import com.jackemate.appberdi.entities.ContentMediateca
import com.jackemate.appberdi.utils.transparentStatusBar

class MediatecaStart : AppCompatActivity() {

    private lateinit var binding : ActivityMediatecaStartBinding
    private val viewModel: MediatecaStartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getContents()

        var listContents : List<ContentMediateca> = emptyList()

        viewModel.contents.observe(this) {
            listContents = it
            binding.gridContents.adapter = GVAdapter(this,listContents)
        }

        binding.gridContents.onItemClickListener = AdapterView.OnItemClickListener{_, _, pos, _ ->
            if (!listContents.isNullOrEmpty()){
                val title : String = listContents[pos].title

                val intent = Intent(this, Mediateca::class.java)
                intent.putExtra("title", title)
                startActivity(intent)
            }
        }
    }

}