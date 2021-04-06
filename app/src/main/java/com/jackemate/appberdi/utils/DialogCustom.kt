package com.jackemate.appberdi.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import com.jackemate.appberdi.databinding.DialogCustomBinding

class DialogCustom(){

    private lateinit var binding : DialogCustomBinding
    private lateinit var dialog : Dialog

    constructor(context: Context, activity: Activity) : this(){
        val inflater : LayoutInflater = LayoutInflater.from(context)
        binding = DialogCustomBinding.inflate(inflater)
        dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun make(){
        dialog.setContentView(binding.root)
        dialog.show()
    }
    fun cancel() = dialog.cancel()

    fun getInput() : String = binding.inputName.editText?.text.toString()

    fun getSave() : Button = binding.save

    fun setAnimation(rawRes : Int) = binding.animation.setAnimation(rawRes)

    fun setText(text : String) { binding.title.text = text }





}


