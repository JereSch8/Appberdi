package com.jackemate.appberdi.ui.cultural

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityCulturalActivitiesBinding
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.transparentStatusBar

class CulturalActivity : AppCompatActivity() {
    private val viewModel: CulturalViewModel by viewModels()
    private lateinit var binding: ActivityCulturalActivitiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityCulturalActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.title.text = getString(R.string.cultural_activities_title)
        binding.header.back.setOnClickListener { finish() }

        observe(viewModel.getCulturalActivities()) {
            binding.attractionList.layoutManager = LinearLayoutManager(this)
            binding.attractionList.adapter = CulturalAdapter(it)
        }
    }

}