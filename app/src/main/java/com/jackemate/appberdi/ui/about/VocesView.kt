package com.jackemate.appberdi.ui.about

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.jackemate.appberdi.R
import com.jackemate.appberdi.utils.dp

class VocesView : LinearLayout {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        orientation = VERTICAL
        gravity = Gravity.CENTER
        setPadding(dp(16), dp(8), dp(16), dp(8))
        val textFont = ResourcesCompat.getFont(context,R.font.montserrat_regular)
        val titleFont = ResourcesCompat.getFont(context,R.font.montserrat_bold)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VocesView,
            0, 0
        ).apply {
            try {
                val entries = getTextArray(R.styleable.VocesView_android_entries) ?: return
                for (i in entries) {
                    val a = i.split('|')
                    val title = a[0]
                    val value = a[1]

                    val titleTV = TextView(context)
                    titleTV.text = title
//                    titleTV.gravity = Gravity.CENTER
                    titleTV.typeface = titleFont
                    titleTV.setTextIsSelectable(true)
                    titleTV.setPadding(0, dp(6), 0, 0)
                    addView(titleTV)

                    val valueTV = TextView(context)
                    valueTV.text = value
//                    valueTV.gravity = Gravity.CENTER
                    valueTV.typeface = textFont
                    valueTV.setTextIsSelectable(true)
                    addView(valueTV)

                }
            } finally {
                recycle()
            }
        }
    }
}
