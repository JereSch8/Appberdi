package com.jackemate.appberdi.ui.attractions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat.startActivity
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ItemSocialBinding
import com.jackemate.appberdi.domain.entities.Link
import java.util.*

class ItemSocial : LinearLayoutCompat {
    private val binding = ItemSocialBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun set(link: Link) {
        Log.i("SET", link.toString())
        binding.openSocial.text = link.rel.capitalize(Locale("es"))
        val icon = getDrawableFromRel(link.rel)
        binding.openSocial.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        binding.root.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(link.href))
            try {
                startActivity(context, i, null)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No se pudo abrir la url: ${link.href}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getDrawableFromRel(rel: String): Int {
        Log.i("getDrawableFromHref", rel)
        return when (rel) {
            "facebook" -> R.drawable.ic_facebook
            "instagram" -> R.drawable.ic_instagram
            else -> R.drawable.ic_forward
        }
    }
}