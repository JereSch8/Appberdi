package com.jackemate.appberdi.ui.shared

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ViewTitleBinding
import com.jackemate.appberdi.utils.dp

class TitleView : ConstraintLayout {
    val binding = ViewTitleBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TitleView,
            0, 0
        ).apply {
            try {
                binding.title.text = getString(R.styleable.TitleView_title)
            } finally {
                recycle()
            }
        }
    }

    fun withText(text: String): TitleView {
        binding.title.text = text
        return this
    }

    fun withPadding(horizontal: Int): TitleView {
        setPadding(dp(horizontal), 0, dp(horizontal), 0)
        return this
    }

    fun with(block: TitleView.() -> Unit): TitleView {
        block()
        return this
    }
}
