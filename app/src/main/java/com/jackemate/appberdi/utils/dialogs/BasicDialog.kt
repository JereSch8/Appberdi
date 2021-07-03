package com.jackemate.appberdi.utils.dialogs

import android.content.Context
import android.text.InputType
import android.view.View
import com.jackemate.appberdi.databinding.DialogCustomBinding
import com.jackemate.appberdi.utils.onTextChanged

class BasicDialog(context: Context) : DialogBuilder(context) {
    override val binding = DialogCustomBinding.inflate(inflater)

    fun setInputTypeNumber(): BasicDialog {
        setInputType(InputType.TYPE_CLASS_NUMBER)
        return this
    }

    fun setInputTypeText(): BasicDialog {
        setInputType(InputType.TYPE_CLASS_TEXT)
        return this
    }

    private fun setInputType(inputType: Int) {
        binding.inputName.visibility = View.VISIBLE
        binding.inputName.editText?.inputType = inputType
    }

    fun getInput(): String = binding.inputName.editText?.text.toString()

    fun setInputListener(listener: (BasicDialog, CharSequence) -> Unit): BasicDialog {
        binding.inputName.editText?.onTextChanged {
            listener(this, it)
        }
        return this
    }

    fun setSaveEnabled(isEnabled: Boolean): BasicDialog {
        binding.save.isEnabled = isEnabled
        return this
    }

    fun setSaveListener(dialog: (BasicDialog) -> Unit): BasicDialog {
        binding.save.setOnClickListener { dialog(this) }
        return this
    }

    fun setText(text: String): BasicDialog {
        binding.title.text = text
        return this
    }

    fun setHintText(text: String): BasicDialog {
        binding.inputName.hint = text
        return this
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}