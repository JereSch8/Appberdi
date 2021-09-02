package com.jackemate.appberdi.ui.view_contents

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.SiteImageFragmentBinding
import com.jackemate.appberdi.utils.IntentName
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.share
import com.jackemate.appberdi.utils.transparentStatusBar

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        val binding = SiteImageFragmentBinding.inflate(layoutInflater)

        val title = intent.getStringExtra(IntentName.TITLE).toString()
        val description = intent.getStringExtra(IntentName.DESCRIPTION).toString()
        val href = intent.getStringExtra(IntentName.HREF).toString()

        setContentView(binding.root)

        binding.title.text = title
        binding.transcription.text = description

        if (href == IntentName.NON_VALUE)
            binding.btnShare.visibility = View.INVISIBLE
        else{
            binding.scrollView.post {
                val layout = binding.playerContainer.layoutParams
                layout.height = binding.scrollView.height - binding.title.height * 2
                binding.playerContainer.layoutParams = layout
                Log.w(TAG, "set height: ${layout.height}")
            }

            binding.btnShare.setOnClickListener { share(title, href) }

            Glide.with(this)
                .load(href)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.loading)
                .centerCrop()
                .into(binding.img)
        }

    }

}