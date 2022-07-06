package com.jackemate.appberdi.ui.shared.contents.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityContentAudioBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.contents.ARG_CONTENT
import com.jackemate.appberdi.ui.shared.contents.fragments.SiteAudioFragment
import com.jackemate.appberdi.utils.transparentStatusBar

class AudioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentAudioBinding
    private lateinit var content: Content.Audio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityContentAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        content = intent?.getSerializableExtra(ARG_CONTENT) as Content.Audio?
            ?: throw Exception("No argument ARG_CONTENT")

        if (savedInstanceState == null) {
            val bundle = bundleOf(ARG_CONTENT to content)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SiteAudioFragment>(R.id.fragment_container_view, args = bundle)
            }
        }

        binding.header.text = getString(R.string.estas_escuchando)

    }

}