package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jackemate.appberdi.domain.entities.Content

const val ARG_CONTENT = "content"
const val ARG_INSET = "bottom_inset"

open class ContentPageFragment : Fragment() {
    lateinit var content: Content

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content = arguments?.getSerializable(ARG_CONTENT) as Content?
            ?: throw Exception("No argument ARG_CONTENT")
    }
}