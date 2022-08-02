package com.jackemate.appberdi.ui.shared.dialogs

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import com.jackemate.appberdi.databinding.DialogCustomBinding
import com.jackemate.appberdi.utils.onTextChanged
import com.jackemate.appberdi.utils.visible

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

    fun setButtonEnabled(isEnabled: Boolean): BasicDialog {
        binding.button.isEnabled = isEnabled
        return this
    }

    fun setButtonText(text: String): BasicDialog {
        binding.button.text = text
        return this
    }

    fun setButtonListener(dialog: (BasicDialog) -> Unit): BasicDialog {
        binding.button.setOnClickListener { dialog(this) }
        return this
    }

    fun setTitle(text: String): BasicDialog {
        binding.title.text = text
        return this
    }

    fun setSubtitle(text: String): BasicDialog {
        binding.subtitle.visible(text.isNotEmpty())
        binding.subtitle.text = text
        return this
    }

    fun setHintText(text: String): BasicDialog {
        binding.inputName.hint = text
        return this
    }

    fun setInputText(text: String): BasicDialog {
        binding.inputName.editText?.setText(text)
        return this
    }

    fun requestFocus(): BasicDialog {
        binding.inputName.editText?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
        binding.inputName.editText?.requestFocus()
        return this
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}