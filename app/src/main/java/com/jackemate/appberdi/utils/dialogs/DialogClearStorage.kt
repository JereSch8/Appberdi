package com.jackemate.appberdi.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.CheckBox
import com.jackemate.appberdi.databinding.DialogClearstorageBinding

class DialogClearStorage(){

    private lateinit var binding : DialogClearstorageBinding
    private lateinit var dialog : Dialog

    constructor(context: Context, activity: Activity) : this(){
        val inflater : LayoutInflater = LayoutInflater.from(context)
        binding = DialogClearstorageBinding.inflate(inflater)
        dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun make(){
        dialog.setContentView(binding.root)
        dialog.show()
    }

    fun cancel() = dialog.cancel()

    fun isCheckIMG()   : Boolean = binding.selectIMG.isChecked
    fun isCheckVIDEO() : Boolean = binding.selectVIDEO.isChecked
    fun isCheckAUDIO() : Boolean = binding.selectAUDIO.isChecked

    fun setTextIMG(text: String)   { binding.selectIMG.text = text }
    fun setTextVIDEO(text: String) { binding.selectVIDEO.text = text }
    fun setTextAUDIO(text: String) { binding.selectAUDIO.text = text }

    fun getCBimg()   : CheckBox = binding.selectIMG
    fun getCBvideo() : CheckBox = binding.selectVIDEO
    fun getCBaudio() : CheckBox = binding.selectAUDIO

    fun getSave() : Button = binding.save

    fun setAnimation(rawRes : Int) = binding.animation.setAnimation(rawRes)

    fun setText(text : String) { binding.title.text = text }

    fun setFinishMessage(text: String) { binding.finishMessage.text = text }
}