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
import android.widget.Toast
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogClearstorageBinding
import com.jackemate.appberdi.io.BasicIO
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.toInt
import com.jackemate.appberdi.utils.toRoundString

class DialogClearStorage(val activity: Activity) {
    private var binding: DialogClearstorageBinding
    private var dialog: Dialog = Dialog(activity)
    private var space     = 0.0
    private val sizeIMG   = BasicIO.sizeDirIMG(activity)
    private val sizeVIDEO = BasicIO.sizeDirVIDEO(activity)
    private val sizeAUDIO = BasicIO.sizeDirAUDIO(activity)

    init {
        val inflater: LayoutInflater = LayoutInflater.from(activity)
        binding = DialogClearstorageBinding.inflate(inflater)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        if (LocalInfo(activity).getAvatar() != -8)
            setAnimation(LocalInfo(activity).getAvatar())
        else
            setAnimation(R.raw.astronaut_dog)

        init()
        initListeners()
        setSave()
    }

    fun show() {
        dialog.setContentView(binding.root)
        dialog.show()
    }

    fun setOnDismiss( listener:()-> Unit) : DialogClearStorage{
        dialog.setOnDismissListener { listener() }
        return this
    }

    private fun init() {
        setText("Selecciona el tipo de contenido que desea eliminar para liberar espacio.")
        setTextIMG("Eliminar imagenes  $sizeIMG MB.")
        setTextVIDEO("Eliminar videos  $sizeVIDEO MB.")
        setTextAUDIO("Eliminar audios  $sizeAUDIO MB.")
        getSave().text = "Eliminar ${space.toRoundString()} MB"
    }

    private fun initListeners() {
        getCBimg().setOnCheckedChangeListener { _, isChecked ->
            space += isChecked.toInt() * sizeIMG
            updateFinishMessage()
        }
        getCBvideo().setOnCheckedChangeListener { _, isChecked ->
            space += isChecked.toInt() * sizeVIDEO
            updateFinishMessage()
        }
        getCBaudio().setOnCheckedChangeListener { _, isChecked ->
            space += isChecked.toInt() * sizeAUDIO
            updateFinishMessage()
        }
    }

    private fun setSave() {
        getSave().setOnClickListener {
            if (isCheckIMG())   BasicIO.deleteDirIMG(activity)
            if (isCheckVIDEO()) BasicIO.deleteDirVIDEO(activity)
            if (isCheckAUDIO()) BasicIO.deleteDirAUDIO(activity)

            dialog.cancel()
        }
    }

    private fun updateFinishMessage() {
        setFinishMessage("${space.toRoundString()} [MB] serán eliminados. Perderas los contenidos y deberás volver a descargarlos")
        getSave().text = "Eliminar ${space.toRoundString()} MB"
    }

    private fun isCheckIMG(): Boolean = binding.selectIMG.isChecked
    private fun isCheckVIDEO(): Boolean = binding.selectVIDEO.isChecked
    private fun isCheckAUDIO(): Boolean = binding.selectAUDIO.isChecked

    private fun setTextIMG(text: String) { binding.selectIMG.text = text }

    private fun setTextVIDEO(text: String) { binding.selectVIDEO.text = text }

    private fun setTextAUDIO(text: String) { binding.selectAUDIO.text = text }

    private fun getCBimg(): CheckBox = binding.selectIMG
    private fun getCBvideo(): CheckBox = binding.selectVIDEO
    private fun getCBaudio(): CheckBox = binding.selectAUDIO

    private fun getSave(): Button = binding.save

    private fun setAnimation(rawRes: Int) = binding.animation.setAnimation(rawRes)

    private fun setText(text: String) { binding.title.text = text  }

    private fun setFinishMessage(text: String) { binding.finishMessage.text = text }
}