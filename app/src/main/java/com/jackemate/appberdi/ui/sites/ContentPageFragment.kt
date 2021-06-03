package com.jackemate.appberdi.ui.sites

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.jackemate.appberdi.R
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.utils.TAG

const val ARG_CONTENT = "content"
const val ARG_INSET = "bottom_inset"

open class ContentPageFragment : Fragment() {
    lateinit var content: Content

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.site_audio_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Para el caso del reproductor de audio
        // necesitamos reajustar en runtime el height de un layout
        // para que ocupe toda la pantalla y lograr un detalle visual.
        // Tal vez no es el lugar adecuado
        // pero tal vez otras pantallas van a necesitar lo mismo
        arguments?.takeIf { it.containsKey(ARG_INSET) }?.apply {
            val layout = requireView().findViewById<ViewGroup>(R.id.player_container)
            if (layout != null) {
                changeHeight(layout, getInt(ARG_INSET))
            }
        }

        content = arguments?.getSerializable(ARG_CONTENT) as Content?
            ?: throw Exception("No argument ARG_CONTENT")
    }

    // Tal vez haya una mejor forma de hacer esto
    // https://stackoverflow.com/a/25533409
    private fun changeHeight(layout: ViewGroup, inset: Int) {
        Log.v(TAG, "changeHeight: !!!!!!!!!!!!!!!!!!!")
        val wm: WindowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        val point = Point()
        try {
            display.getSize(point)
        } catch (ignore: NoSuchMethodError) { // Older device
            point.x = display.width
            point.y = display.height
        }

        val lp: ViewGroup.LayoutParams = layout.layoutParams
        lp.height = point.y - inset
        Log.v(TAG, "changeHeight: ${lp.height}")
        layout.layoutParams = lp
    }
}