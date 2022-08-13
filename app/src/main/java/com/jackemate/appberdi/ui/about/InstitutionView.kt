package com.jackemate.appberdi.ui.about

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.jackemate.appberdi.R
import com.jackemate.appberdi.utils.dp

class InstitutionView : LinearLayout {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        orientation = VERTICAL
        gravity = Gravity.START
        setPadding(dp(16), dp(8), dp(16), dp(16))

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VocesView,
            0, 0
        ).apply {
            try {
                val entries = getTextArray(R.styleable.VocesView_android_entries) ?: return
                val textFont = ResourcesCompat.getFont(context,R.font.montserrat_regular)
                val subtitleFont = ResourcesCompat.getFont(context,R.font.montserrat_semibold)

                for (i in entries) {
                    val a = i.split('|')
                    val key = a[0]
                    val value = a[1]
                    if (key == "image") {
                        val img = ImageView(context)
                        val drawableResourceId = this.resources.getIdentifier(
                            value,
                            "drawable",
                            context.packageName
                        )
                        img.setImageResource(drawableResourceId)
                        addView(img)
                        img.layoutParams.height = dp(64)
                        img.layoutParams.width = dp(64)
                        img.requestLayout()
                    } else {
                        val valueTV = TextView(context)
                        valueTV.text = value
//                        valueTV.gravity = Gravity.CENTER
                        valueTV.typeface = textFont
                        valueTV.setTextIsSelectable(true)

                        when (key) {
                            "people" -> addView(valueTV)
                            "site" -> {
//                                valueTV.textAlignment = TEXT_ALIGNMENT_CENTER
                                valueTV.typeface = subtitleFont
                                valueTV.setPadding(0, dp(4), 0, 0)
                                addView(valueTV)
                            }
                            else -> {
                                val subtitle = TextView(context)
                                subtitle.typeface = subtitleFont
//                                subtitle.gravity = Gravity.CENTER
                                subtitle.setTextIsSelectable(true)
                                subtitle.setPadding(0, dp(6), 0, 0)
                                subtitle.text = key
                                addView(subtitle)

                                addView(valueTV)
                            }
                        }
                    }
                }
            } finally {
                recycle()
            }
        }
    }
}
