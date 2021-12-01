package com.jackemate.appberdi.ui.shared.contents

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jackemate.appberdi.entities.Content

const val ARG_CONTENT = "content"
const val ARG_ID_SITE = "idSite"

open class ContentPageFragment : Fragment() {
    lateinit var content: Content
    var idSite: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content = requireArguments().getSerializable(ARG_CONTENT) as Content?
            ?: throw Exception("No argument ARG_CONTENT")

        idSite = requireArguments().getSerializable(ARG_ID_SITE) as String?
//            ?: throw Exception("No argument ARG_ID_SITE")
    }
}