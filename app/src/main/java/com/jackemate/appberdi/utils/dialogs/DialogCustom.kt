package com.jackemate.appberdi.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogCustomBinding
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.onTextChanged

class DialogCustom(val activity: Activity){
    private var binding : DialogCustomBinding
    private var dialog : Dialog
    private val localInfo: LocalInfo = LocalInfo(activity.baseContext)

    init {
        val inflater : LayoutInflater = LayoutInflater.from(activity)
        binding = DialogCustomBinding.inflate(inflater)
        dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        if(localInfo.getAvatar() != -8)
            setAnimation(localInfo.getAvatar())
        else
            setAnimation(R.raw.astronaut_dog)
    }

    fun show() {
        dialog.setContentView(binding.root)
        dialog.show()
    }

    fun cancel() = dialog.cancel()

    fun setInputTypeNumber() : DialogCustom{
        setInputType(InputType.TYPE_CLASS_NUMBER)
        return this
    }

    fun setInputTypeText() : DialogCustom{
        setInputType(InputType.TYPE_CLASS_TEXT)
        return this
    }

    private fun setInputType(inputType : Int){
        binding.inputName.visibility = View.VISIBLE
        binding.inputName.editText?.inputType = inputType
    }

    fun getInput() : String = binding.inputName.editText?.text.toString()

    fun setInputListener( listener:(DialogCustom, CharSequence)-> Unit) : DialogCustom{
        binding.inputName.editText?.onTextChanged {
            listener(this, it )
        }
        return this
    }

    fun setSaveEnabled( isEnabled : Boolean) : DialogCustom{
        binding.save.isEnabled = isEnabled
        return this
    }

    fun setSaveListener( dialog:(DialogCustom)-> Unit) : DialogCustom{
        binding.save.setOnClickListener { dialog(this) }
        return this
    }

    private fun setAnimation(rawRes : Int) : DialogCustom {
        binding.animation.setAnimation(rawRes)
        return this
    }

    fun setText(text : String) : DialogCustom {
        binding.title.text = text
        return this
    }

    fun setHintText(text: String) : DialogCustom {
        binding.inputName.hint = text
        return this
    }
}