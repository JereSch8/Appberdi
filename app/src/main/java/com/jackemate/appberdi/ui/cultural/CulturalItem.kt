package com.jackemate.appberdi.ui.cultural

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.R
import com.jackemate.appberdi.entities.CulturalActivity
import com.jackemate.appberdi.entities.Link
import com.jackemate.appberdi.ui.shared.TitleView
import com.jackemate.appberdi.utils.addRipple
import com.jackemate.appberdi.utils.copyToClipboard
import com.jackemate.appberdi.utils.dp

class CulturalItem : LinearLayoutCompat {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        orientation = VERTICAL
        setPadding(0, dp(16), 0, dp(16))
    }

    fun set(item: CulturalActivity) {
        removeAllViews()
        addView(TitleView(context).withText(item.name).withPadding(16))
        getStreetView(item)?.let(::addView)
        getPhoneView(item)?.let(::addView)
        getMailView(item)?.let(::addView)
        getLinkView(R.drawable.ic_social_face, item.face)?.let(::addView)
        getLinkView(R.drawable.ic_social_insta, item.insta)?.let(::addView)
        getLinkView(R.drawable.ic_social_web, item.web)?.let(::addView)
    }

    private fun getStreetView(item: CulturalActivity): AppCompatButton? {
        if (item.street.isEmpty()) return null

        val button = getBaseButton(R.drawable.ic_social_pin, item.street)
        button.setOnClickListener {
            if (item.pos == null) {
                context.copyToClipboard(item.street)
                Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
            } else {
                openGoogleMap(item.pos, item)
            }
            Log.e("TARST", item.pos.toString())
        }
        return button
    }

    private fun openGoogleMap(pos: GeoPoint, item: CulturalActivity) {
        val uri = Uri.parse("geo:${pos.latitude},${pos.longitude}?q=" + Uri.encode(item.name))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.resolveActivity(context.packageManager)?.let {
            context.startActivity(intent)
        }
    }

    private fun getPhoneView(item: CulturalActivity): AppCompatButton? {
        if (item.phone.isEmpty()) return null
        return getBaseButton(R.drawable.ic_social_phone, item.phone).apply {
            setOnClickListener {
                // Por si tiene algo entre paréntesis (Museo de la Reforma Univesitaria)
                val number = item.phone.split("(").first()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$number")
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    private fun getMailView(item: CulturalActivity): AppCompatButton? {
        if (item.email.isEmpty()) return null
        return getBaseButton(R.drawable.ic_social_email, item.email).apply {
            setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:${item.email}")
                if (intent.resolveActivity(context.packageManager) != null) {
                    ContextCompat.startActivity(context, intent, null)
                }
            }
        }
    }

    private fun getLinkView(drawable: Int, link: Link?): AppCompatButton? {
        return link?.let {
            getBaseButton(drawable, link.rel).apply {
                setOnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.href))
                        ContextCompat.startActivity(context, intent, null)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            "No se pudo abrir la url: ${link.href}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun getBaseButton(drawable: Int, value: String) = AppCompatButton(context).apply {
        setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
        text = value
        addRipple()
        setPadding(dp(16), 0, dp(16), 0)
        textAlignment = TEXT_ALIGNMENT_TEXT_START
        isAllCaps = false
        compoundDrawablePadding = dp(8)
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)
    }

}