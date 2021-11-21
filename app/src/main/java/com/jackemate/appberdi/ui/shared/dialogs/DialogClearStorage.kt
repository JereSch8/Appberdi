package com.jackemate.appberdi.ui.shared.dialogs

import android.app.Activity
import android.widget.Button
import android.widget.CheckBox
import com.jackemate.appberdi.data.CacheRepository
import com.jackemate.appberdi.databinding.DialogClearstorageBinding
import com.jackemate.appberdi.io.BasicIO
import com.jackemate.appberdi.ui.shared.DialogBuilder
import com.jackemate.appberdi.utils.toInt
import com.jackemate.appberdi.utils.toRoundString

class DialogClearStorage(val activity: Activity) : DialogBuilder(activity) {
    override val binding = DialogClearstorageBinding.inflate(inflater)
    private val cacheRepo = CacheRepository(activity)

    private var space = 0.0
    private val sizeIMG = cacheRepo.sizeOf("Imagen")
    private val sizeVIDEO = cacheRepo.sizeOf("Vídeo")
    private val sizeAUDIO = cacheRepo.sizeOf("Audio")

    init {
        init()
        initListeners()
    }

    private fun init() {
        binding.title.text =
            "Selecciona el tipo de contenido que desea eliminar para liberar espacio."
        binding.selectIMG.text = "Eliminar imagenes  ${sizeIMG.toRoundString()} MB."
        binding.selectVIDEO.text = "Eliminar videos  ${sizeVIDEO.toRoundString()} MB."
        binding.selectAUDIO.text = "Eliminar audios  ${sizeAUDIO.toRoundString()} MB."
        binding.button.text = "Eliminar ${space.toRoundString()} MB"

        binding.button.setOnClickListener {
            if (isCheckIMG()) cacheRepo.clear("Imagen")
            if (isCheckVIDEO()) cacheRepo.clear("Vídeo")
            if (isCheckAUDIO()) cacheRepo.clear("Audio")
            dismiss()
        }
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

    private fun updateFinishMessage() {
        binding.finishMessage.text =
            "${space.toRoundString()}MB serán eliminados. Perderas los contenidos y deberás volver a descargarlos"
        binding.button.text = "Eliminar ${space.toRoundString()} MB"
    }

    private fun isCheckIMG(): Boolean = binding.selectIMG.isChecked
    private fun isCheckVIDEO(): Boolean = binding.selectVIDEO.isChecked
    private fun isCheckAUDIO(): Boolean = binding.selectAUDIO.isChecked

    private fun getCBimg(): CheckBox = binding.selectIMG
    private fun getCBvideo(): CheckBox = binding.selectVIDEO
    private fun getCBaudio(): CheckBox = binding.selectAUDIO

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}