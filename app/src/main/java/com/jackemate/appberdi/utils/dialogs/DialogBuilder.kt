package com.jackemate.appberdi.utils.dialogs

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
import com.jackemate.appberdi.utils.LocalInfo

abstract class DialogBuilder(context: Context): Dialog(context) {

    protected val inflater: LayoutInflater = LayoutInflater.from(context)
    protected abstract val binding: ViewBinding
    protected val localInfo: LocalInfo = LocalInfo(context.applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if(localInfo.getAvatar() != -8)
            setAnimation(localInfo.getAvatar())
        else
            setAnimation(R.raw.astronaut_dog)
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