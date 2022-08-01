package com.jackemate.appberdi.ui.about

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.jackemate.appberdi.R
import com.jackemate.appberdi.utils.dp

class InstitutionView :  LinearLayout {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        orientation = VERTICAL
        gravity = Gravity.CENTER
        setPadding(dp(16))

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VocesView,
            0, 0
        ).apply {
            try {
                val entries = getTextArray(R.styleable.VocesView_android_entries)
                if (entries != null) {
                    val linearLayout = LinearLayout(context)
                    linearLayout.orientation = HORIZONTAL
                    for (i in entries) {
                        val a = i.split('|')
                        val key = a[0]
                        val value = a[1]
                        if (key == "image"){
                            val img = ImageView(context)
                            val drawableResourceId = this.resources.getIdentifier(
                                value,
                                "drawable",
                                context.packageName
                            )
                            img.setImageResource(drawableResourceId)
                            linearLayout.addView(img)
                            img.layoutParams.height = 120
                            img.layoutParams.width = 120
                            img.requestLayout()
                        }
                        else{
                            val valueTV = TextView(context)
                            valueTV.text = value
                            if (key == "site") {
                                valueTV.gravity =
                                    Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
                                valueTV.textAlignment = TEXT_ALIGNMENT_CENTER
                                valueTV.setTypeface(valueTV.typeface, Typeface.BOLD)
                                linearLayout.addView(valueTV)
                                valueTV.layoutParams.height = 120
                                valueTV.layoutParams.width = LayoutParams.MATCH_PARENT
                                valueTV.requestLayout()
                                addView(linearLayout)
                            }
                            else {
                                valueTV.gravity = Gravity.CENTER
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
