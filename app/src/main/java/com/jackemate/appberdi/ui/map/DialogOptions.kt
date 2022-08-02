package com.jackemate.appberdi.ui.map

import android.app.Activity
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogOptionsBinding
import com.jackemate.appberdi.ui.shared.dialogs.DialogBuilder

class DialogOptions(val activity: Activity, private var isVirtual: Boolean) :
    DialogBuilder(activity) {
    override val binding = DialogOptionsBinding.inflate(inflater)

    init {
        binding.button.setOnClickListener {
            dismiss()
        }
        updateUI()
    }

    private fun updateUI() {
        binding.btnVirtualMode.text = context.getString(
            if (isVirtual) R.string.opciones_virtual_on
            else R.string.opciones_virtual_off
        )
        binding.btnVirtualMode.setIconResource(
            if (isVirtual) R.drawable.ic_gps_off
            else R.drawable.ic_gps_on
        )
        binding.tvVirtualMode.text = context.getString(
            if (isVirtual) R.string.opciones_virtual_on_desc
            else R.string.opciones_virtual_off_desc
        )
    }

    fun setVirtualModeListener(fn: (Boolean) -> Unit): DialogOptions {
        binding.btnVirtualMode.setOnClickListener {
            isVirtual = !isVirtual
            updateUI()
            fn(isVirtual)
//            dismiss()
        }
        return this
    }

    fun setStopButtonListener(fn: () -> Unit): DialogOptions {
        binding.stopTour.setOnClickListener {
            fn()
            dismiss()
        }
        return this
    }

//    fun setVirtualMode(activated: Boolean): DialogOptions {
//        virtualModeActivated = activated
//
//        return this
//    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }
}