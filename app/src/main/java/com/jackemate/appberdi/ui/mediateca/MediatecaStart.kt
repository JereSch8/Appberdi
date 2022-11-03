package com.jackemate.appberdi.ui.mediateca

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMediatecaStartBinding
import com.jackemate.appberdi.entities.ContentMediateca
import com.jackemate.appberdi.utils.finishWithToast
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.transparentStatusBar

class MediatecaStart : AppCompatActivity() {

    private lateinit var binding: ActivityMediatecaStartBinding
    private val viewModel: MediatecaStartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMediatecaStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.title.text = getString(R.string.mediateca_title)
        binding.header.back.setOnClickListener { finish() }
        binding.contents.layoutManager = GridLayoutManager(this, 2)

        observe(viewModel.getItems()) {
            if (it == null) return@observe finishWithToast()
            binding.contents.adapter = MediatecaStartAdapter(it, this::onSelected)
        }
    }

    private fun onSelected(item: ContentMediateca) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "mediateca")
            param(FirebaseAnalytics.Param.ITEM_ID, item.title)
        }

        startActivity(Intent(this, MediatecaSiteActivity::class.java).apply {
            putExtra("title", item.title)
        })
    }

}