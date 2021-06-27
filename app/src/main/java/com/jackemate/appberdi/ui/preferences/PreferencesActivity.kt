package com.jackemate.appberdi.ui.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityPreferencesBinding
import com.jackemate.appberdi.io.*
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.dialogs.DialogClearStorage
import com.jackemate.appberdi.utils.dialogs.DialogCustom
import com.jackemate.appberdi.utils.toRoundString

@SuppressLint("SetTextI18n")
class PreferencesActivity : AppCompatActivity() {
    private val viewModel: PreferencesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameUser.text = viewModel.getName()

        if (viewModel.getLimitStorage() != -8)
            binding.setLimitStorage.text = "Límite de almacenamiento: ${viewModel.getLimitStorage()} MB."
        if (viewModel.getLimitMovil() != -8)
            binding.setLimitMovil.text = "Límite de datos: ${viewModel.getLimitMovil()} MB."

        binding.cleanStorage.text = "Borrar datos almacenados: ${viewModel.getSizeStorage()} MB."

        binding.progressSite.apply {
            setProgressWithAnimation(viewModel.getProgressSite(), 3000) // =3s
            progressMax = viewModel.getAmountSites()
            binding.txtProgressSite.text = "${viewModel.getProgressSite().toInt()}/${progressMax.toInt()}"
        }

        binding.progressTreasure.apply {
            setProgressWithAnimation(viewModel.getProgressTreasure(), 3000) // =3s
            progressMax = viewModel.getAmountTreasure()
            binding.txtProgressTreasure.text = "${viewModel.getProgressTreasure().toInt()}/${progressMax.toInt()}"
        }

        binding.nameUser.setOnClickListener { createDialogChangeName( binding ) }

        binding.setLimitStorage.setOnClickListener { createDialogLimit(binding, true) }

        binding.cleanStorage.setOnClickListener { createClearStorageDialog(binding) }

        binding.setLimitMovil.setOnClickListener { createDialogLimit( binding, false) }

        binding.clearProgressSite.setOnClickListener { createDialogDelete(binding) }

        binding.autoPlayAudio.isChecked = viewModel.getAutoPlayAudio()
        binding.autoPlayVideo.isChecked = viewModel.getAutoPlayVideo()

        binding.autoPlayAudio.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoPlayAudio(isChecked)
            if (isChecked)
                Toast.makeText(
                    this,
                    "Se habilitó la reproducción automatica de audio.",
                    Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(
                    this,
                    "Se deshabilitó la reproducción automatica de audio.",
                    Toast.LENGTH_SHORT
                ).show()
        }
        binding.autoPlayVideo.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoPlayVideo(isChecked)
            if (isChecked)
                Toast.makeText(
                    this,
                    "Se habilitó la reproducción automatica de video.",
                    Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(
                    this,
                    "Se deshabilitó la reproducción automatica de video.",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun createDialogChangeName(binding : ActivityPreferencesBinding){
        val dialog = DialogCustom(this, this)
        dialog.setText("Cambiar nombre")
        dialog.getSave().setOnClickListener{
            val name : String = dialog.getInput()
            if(name.length in 3..15){
                viewModel.setName(name)
                binding.nameUser.text = name
                Toast.makeText(this, "Genial tu nuevo nombre $name !!!", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
            else
                Toast.makeText(this,"Debes Ingresar un nombre válido, entre 3 y 15 caracteres.", Toast.LENGTH_SHORT).show()
        }
        dialog.setAnimation(R.raw.astronaut_dog)
        dialog.make()
    }

    private fun createDialogLimit(binding : ActivityPreferencesBinding, isStorage : Boolean){
        val dialog = DialogCustom(this, this)
        val text : String = ("Establecer limite de " + (if (isStorage) "almacenamiento, " else "datos, ") + "en MB (MegaBytes).")
        dialog.setText(text)
        dialog.setHintText("Límite de "+if (isStorage) "almacenamiento." else "datos.")
        dialog.getSave().setOnClickListener{
            val limit = if (dialog.getInput().isNotBlank()) dialog.getInput() else " "
            if (limit.isDigitsOnly() && limit.toInt()>20 ){
                if (isStorage){
                    viewModel.setLimitStorage(limit.toInt())
                    binding.setLimitStorage.text = "Límite de almacenamiento: $limit MB."
                    Toast.makeText(this, "Ahora la aplicacion solo puede almacenar $limit MB.", Toast.LENGTH_SHORT).show()
                }
                else{
                    viewModel.setLimitMovil(limit.toInt())
                    binding.setLimitMovil.text = "Límite de datos: $limit MB."
                    Toast.makeText(this, "Ahora la aplicacion solo puede descargar $limit MB.", Toast.LENGTH_SHORT).show()
                }
                dialog.cancel()
            }
            else
                Toast.makeText(this, "$limit debe ser un número mayor a 20 MB.", Toast.LENGTH_SHORT).show()
        }
        dialog.setAnimation(R.raw.astronaut_dog)
        dialog.make()
    }

    private fun createDialogDelete(binding : ActivityPreferencesBinding){
        val dialog = DialogCustom(this, this)
        dialog.setText("Estas a punto de borrar tu progreso. Deberás volver a comenzar.")
        dialog.getEditText()?.visibility = View.GONE
        Log.e(TAG, "createDialogDelete: ENTRO" )
        dialog.getSave().setOnClickListener{
            viewModel.setProgressSite(-8)
            binding.progressSite.apply {
                setProgressWithAnimation(0.1f, 3000) // =3s
                progressMax = viewModel.getAmountSites()
                binding.txtProgressSite.text = "0/${progressMax.toInt()}"
            }
            Toast.makeText(this, "Tu progreso volvió a 0", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        dialog.setAnimation(R.raw.astronaut_dog)
        dialog.make()
    }

    private fun createClearStorageDialog(binding : ActivityPreferencesBinding){
        var space = 0.0
        val dialog = DialogClearStorage(this, this)
        dialog.setText("Selecciona el tipo de contenido que desea eliminar para liberar espacio.")
        dialog.setTextIMG("Eliminar imagenes  ${sizeDirIMG(this)} MB.")
        dialog.setTextVIDEO("Eliminar videos  ${sizeDirVIDEO(this)} MB.")
        dialog.setTextAUDIO("Eliminar audios  ${sizeDirAUDIO(this)} MB.")
        dialog.getSave().text = "Eliminar ${space.toRoundString()} MB"

        dialog.getCBimg().setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                space += sizeDirIMG(this)
            else
                space -= sizeDirIMG(this)
            dialog.setFinishMessage("${space.toRoundString()} [MB] serán eliminados. Perderas los contenidos y deberás volver a descargarlos")
            dialog.getSave().text = "Eliminar ${space.toRoundString()} MB"
        }
        dialog.getCBvideo().setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                space += sizeDirVIDEO(this)
            else
                space -= sizeDirVIDEO(this)
            dialog.setFinishMessage("${space.toRoundString()} [MB] serán eliminados. Perderas los contenidos y deberás volver a descargarlos")
            dialog.getSave().text = "Eliminar ${space.toRoundString()} MB"
        }
        dialog.getCBaudio().setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                space += sizeDirAUDIO(this)
            else
                space -= sizeDirAUDIO(this)
            dialog.setFinishMessage("${space.toRoundString()} [MB] serán eliminados. Perderas los contenidos y deberás volver a descargarlos")
            dialog.getSave().text = "Eliminar ${space.toRoundString()} MB"
        }

        dialog.getSave().setOnClickListener{
            if(dialog.isCheckIMG()) deleteDirIMG(this)
            if(dialog.isCheckVIDEO()) deleteDirVIDEO(this)
            if(dialog.isCheckAUDIO()) deleteDirAUDIO(this)
            binding.cleanStorage.text = "Borrar datos almacenados: ${viewModel.getSizeStorage().toRoundString()} MB."
            Toast.makeText(this, "Se liberaron ${space.toRoundString()} MB.", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        dialog.setAnimation(R.raw.astronaut_dog)
        dialog.make()
    }

}