package com.jackemate.appberdi.ui.about

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ViewStudentBinding
import com.jackemate.appberdi.utils.open
import com.jackemate.appberdi.utils.visible

class StudentView : ConstraintLayout {

    private var binding: ViewStudentBinding =
        ViewStudentBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StudentView,
            0, 0
        ).apply {

            try {
                binding.name.text = getString(R.styleable.StudentView_name)
                binding.facu.text = getString(R.styleable.StudentView_facu)
                setSocial(binding.socialFace, getString(R.styleable.StudentView_social_face))
                setSocial(binding.socialGithub, getString(R.styleable.StudentView_social_github))
                setSocial(binding.socialInsta, getString(R.styleable.StudentView_social_insta))
                setSocial(binding.socialLinkedin, getString(R.styleable.StudentView_social_linkedin))
                setSocial(binding.socialTwitter, getString(R.styleable.StudentView_social_twitter))
            } finally {
                recycle()
            }
        }
    }

    private fun setSocial(button: Button, url: String?) {
        button.visible(!url.isNullOrEmpty())
        button.setOnClickListener { context.open(url) }
    }
}
