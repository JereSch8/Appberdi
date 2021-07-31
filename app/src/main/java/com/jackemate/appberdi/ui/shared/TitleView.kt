package com.jackemate.appberdi.ui.shared

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ViewTitleBinding

class TitleView : ConstraintLayout {

    private lateinit var binding: ViewTitleBinding

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        binding = ViewTitleBinding.inflate(LayoutInflater.from(context), this, true)

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
}
