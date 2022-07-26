package com.jackemate.appberdi.ui.about

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.jackemate.appberdi.R
import com.jackemate.appberdi.utils.PxtoDP

class VocesView : LinearLayout {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        orientation = VERTICAL
        gravity = Gravity.CENTER
        setPadding(context.PxtoDP(16))

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VocesView,
            0, 0
        ).apply {
            try {
                val entries = getTextArray(R.styleable.VocesView_android_entries)
                if (entries != null) {
                    for ( i in entries){
                        val a = i.split('|')
                        val title = a[0]
                        val value = a[1]
                        val titleTV = TextView(context)
                        val valueTV = TextView(context)
                        titleTV.text = title
                        titleTV.gravity = Gravity.CENTER
                        titleTV.setTypeface(titleTV.typeface, Typeface.BOLD)

                        valueTV.text = value
                        valueTV.gravity = Gravity.CENTER

                        addView(titleTV)
                        addView(valueTV)

                    }
                }
            } finally {
                recycle()
            }
        }
    }
}
