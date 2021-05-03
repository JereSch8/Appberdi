package com.jackemate.appberdi.ui.preferences

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityPreferencesBinding
import com.jackemate.appberdi.ui.attractions.AttractionListAdapter
import com.jackemate.appberdi.ui.attractions.AttractionViewModel
import com.jackemate.appberdi.utils.DialogCustom
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.observe
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class PreferencesActivity : AppCompatActivity() {
    private val viewModel: AttractionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameUser.text = LocalInfo(this).getUserName()
        val limitStorage = LocalInfo(this).getLimitStorage()
        val limitMovil = LocalInfo(this).getLimitMovil()
        if (limitStorage != -8)
            binding.setLimitStorage.text = "Límite de almacenamiento: $limitStorage MB."
        if (limitMovil != -8)
            binding.setLimitStorage.text = "Límite de datos: $limitMovil MB."

        binding.progressSite.apply {
            setProgressWithAnimation(8f, 3000) // =3s
            progressMax = 12f
        }

        binding.progressTreasure.apply {
            setProgressWithAnimation(14f, 3000) // =3s
            progressMax = 32f
        }

        binding.nameUser.setOnClickListener { createDialogChangeName(binding) }

        binding.setLimitStorage.setOnClickListener { createDialogLimit(binding, true) }
        binding.setLimitMovil.setOnClickListener { createDialogLimit(binding, false) }

        binding.clearProgressSite.setOnClickListener { createDialogDelete(binding)  }

        binding.autoPlayAudio.isChecked = LocalInfo(this).getAutoPlayAudio()
        binding.autoPlayVideo.isChecked = LocalInfo(this).getAutoPlayVideo()

        binding.autoPlayAudio.setOnCheckedChangeListener { buttonView, isChecked ->
            LocalInfo(this).setAutoPlayAudio(isChecked)
            if (isChecked)
                Toast.makeText(this,"Se habilitó la reproducción automatica de audio.",Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"Se deshabilitó la reproducción automatica de audio.",Toast.LENGTH_SHORT).show()
        }
        binding.autoPlayVideo.setOnCheckedChangeListener { buttonView, isChecked ->
            LocalInfo(this).setAutoPlayVideo(isChecked)
            if (isChecked)
                Toast.makeText(this,"Se habilitó la reproducción automatica de video.",Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"Se deshabilitó la reproducción automatica de video.",Toast.LENGTH_SHORT).show()
        }

    }


    private fun createDialogChangeName(binding : ActivityPreferencesBinding){
        val dialog = DialogCustom(this, this)

        dialog.setText("Cambiar nombre")
        dialog.getSave().setOnClickListener{
            val name : String = dialog.getInput()
            if(name.length in 3..15){
                LocalInfo(this).setUserName(name)
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
                    LocalInfo(this).setLimitStorage(limit.toInt())
                    binding.setLimitStorage.text = "Límite de almacenamiento: $limit MB."
                    Toast.makeText(this, "Ahora la aplicacion solo puede almacenar $limit MB.", Toast.LENGTH_SHORT).show()
                }
                else{
                    LocalInfo(this).setLimitMovil(limit.toInt())
                    binding.setLimitStorage.text = "Límite de datos: $limit MB."
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
        dialog.setText("Estas a punto de borrar tu progreso. Ecribe 'si' (sin ') si estas segura/o de volver a comenzar.")
        dialog.setHintText("Estás segura/o?")
        dialog.getSave().setOnClickListener{
            val input : String = dialog.getInput()
            if(input.equals("si")){
                LocalInfo(this).setProgressSite(0)
                binding.progressSite.apply {
                    setProgressWithAnimation(0.1f, 3000) // =3s
                    progressMax = 12f
                }
                Toast.makeText(this, "Tu progreso volvió a 0", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
            else
                Toast.makeText(this,"Debes Ingresar 'si' para eliminar los datos. (sin las ') ", Toast.LENGTH_LONG).show()
        }
        dialog.setAnimation(R.raw.astronaut_dog)
        dialog.make()
    }

}