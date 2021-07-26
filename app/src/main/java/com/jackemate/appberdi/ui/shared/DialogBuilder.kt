package com.jackemate.appberdi.ui.shared

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.view.Window
import androidx.viewbinding.ViewBinding
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.PreferenceRepository

abstract class DialogBuilder(context: Context): Dialog(context) {

    protected val inflater: LayoutInflater = LayoutInflater.from(context)
    protected abstract val binding: ViewBinding
    protected val preferenceRepo = PreferenceRepository(context.applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAnimation(preferenceRepo.getAvatar() ?: R.raw.astronaut_dog)
    }

    override fun show() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.show()
    }

    fun setOnDismissListener(listener: () -> Unit) : DialogBuilder {
        super.setOnDismissListener{ listener() }
        return this
    }

    abstract fun setAnimation(rawRes : Int) : DialogBuilder

}