package com.jackemate.appberdi.ui.sites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jackemate.appberdi.domain.entities.Content

const val ARG_CONTENT = "content"
const val ARG_ID_SITE = "idSite"

open class ContentPageFragment : Fragment() {
    lateinit var content: Content
    lateinit var idSite: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content = arguments?.getSerializable(ARG_CONTENT) as Content?
            ?: throw Exception("No argument ARG_CONTENT")

        idSite = arguments?.getSerializable(ARG_ID_SITE) as String?
            ?: throw Exception("No argument ARG_ID_SITE")
    }
}