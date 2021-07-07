package com.jackemate.appberdi.ui.shared.dialogs

import android.app.Activity
import android.widget.Button
import android.widget.CheckBox
import com.jackemate.appberdi.databinding.DialogClearstorageBinding
import com.jackemate.appberdi.io.BasicIO
import com.jackemate.appberdi.ui.shared.DialogBuilder
import com.jackemate.appberdi.utils.toInt
import com.jackemate.appberdi.utils.toRoundString

class DialogClearStorage(val activity: Activity) : DialogBuilder(activity) {
    override val binding = DialogClearstorageBinding.inflate(inflater)
    private var space = 0.0
    private val sizeIMG = BasicIO.sizeDirIMG(activity)
    private val sizeVIDEO = BasicIO.sizeDirVIDEO(activity)
    private val sizeAUDIO = BasicIO.sizeDirAUDIO(activity)

    init {
        init()
        initListeners()
        setSave()
    }

    private fun init() {
        binding.title.text =
            "Selecciona el tipo de contenido que desea eliminar para liberar espacio."
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
            if (isCheckIMG()) BasicIO.deleteDirIMG(activity)
            if (isCheckVIDEO()) BasicIO.deleteDirVIDEO(activity)
            if (isCheckAUDIO()) BasicIO.deleteDirAUDIO(activity)

            cancel()
        }
    }

    private fun updateFinishMessage() {
        binding.finishMessage.text =
            "${space.toRoundString()} [MB] serán eliminados. Perderas los contenidos y deberás volver a descargarlos"
        getSave().text = "Eliminar ${space.toRoundString()} MB"
    }

    private fun isCheckIMG(): Boolean = binding.selectIMG.isChecked
    private fun isCheckVIDEO(): Boolean = binding.selectVIDEO.isChecked
    private fun isCheckAUDIO(): Boolean = binding.selectAUDIO.isChecked

    private fun setTextIMG(text: String) {
        binding.selectIMG.text = text
    }

    private fun setTextVIDEO(text: String) {
        binding.selectVIDEO.text = text
    }

    private fun setTextAUDIO(text: String) {
        binding.selectAUDIO.text = text
    }

    private fun getCBimg(): CheckBox = binding.selectIMG
    private fun getCBvideo(): CheckBox = binding.selectVIDEO
    private fun getCBaudio(): CheckBox = binding.selectAUDIO

    private fun getSave(): Button = binding.save

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}