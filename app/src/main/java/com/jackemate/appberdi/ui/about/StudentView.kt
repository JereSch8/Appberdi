package com.jackemate.appberdi.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ViewStudentBinding
import com.jackemate.appberdi.utils.visible

class StudentView : ConstraintLayout {

    private lateinit var binding: ViewStudentBinding

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        binding = ViewStudentBinding.inflate(LayoutInflater.from(context), this, true)

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
        button.setOnClickListener {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
