package com.jackemate.appberdi.ui.preferences

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.android.material.switchmaterial.SwitchMaterial
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityPreferencesBinding
import com.jackemate.appberdi.ui.shared.dialogs.BasicDialog
import com.jackemate.appberdi.ui.shared.dialogs.DialogClearStorage
import com.jackemate.appberdi.ui.shared.dialogs.avatar.DialogAvatars
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.transparentStatusBar

class PreferencesActivity : AppCompatActivity() {
    private val viewModel: PreferencesViewModel by viewModels()
    private lateinit var binding: ActivityPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observe(viewModel.data) { data ->
            binding.apply {
                nameUser.text = data.username
                avatarUser.setAnimation(data.avatar ?: R.raw.astronaut_dog)
                avatarUser.playAnimation()

                setLimitStorage.text = "Límite de almacenamiento: ${data.storageLimit} MB."
                setLimitMovil.text = "Límite de datos: ${data.mobilLimit} MB."
                cleanStorage.text = getString(R.string.borrar_datos_almacenados, data.storageSize)

                progressSite.apply {
                    setProgressWithAnimation(data.siteProgress, 3000) // =3s
                    progressMax = data.siteTotal
                    binding.txtProgressSite.text =
                        "${data.siteProgress.toInt()}/${progressMax.toInt()}"
                }

                progressTreasure.apply {
                    setProgressWithAnimation(data.treasureProgress, 3000) // =3s
                    progressMax = data.treasureTotal
                    binding.txtProgressTreasure.text =
                        "${data.treasureProgress.toInt()}/${progressMax.toInt()}"
                }

                autoPlayAudio.isChecked = data.autoPlayAudio
                autoPlayVideo.isChecked = data.autoPlayVideo
            }

            binding.nameUser.setOnClickListener { createDialogChangeName(data.username) }
        }

        binding.avatarUser.setOnClickListener {
            DialogAvatars(this)
                .setText("Selecciona el avatar que más te guste!")
                .setOnDismissListener {
                    viewModel.load()
                }.show()
        }

        binding.setLimitStorage.setOnClickListener { createDialogLimit(true) }
        binding.cleanStorage.setOnClickListener { createClearStorageDialog() }

        binding.setLimitMovil.setOnClickListener { createDialogLimit(false) }
        binding.clearProgressSite.setOnClickListener { createDialogDelete() }

        binding.autoPlayAudio.setOnClickListener { v ->
            val isChecked = (v as SwitchMaterial).isChecked
            viewModel.setAutoPlayAudio(isChecked)
            Toast.makeText(
                this,
                if (isChecked)
                    "Se habilitó la reproducción automatica de audio."
                else
                    "Se deshabilitó la reproducción automatica de audio.",
                Toast.LENGTH_SHORT
            ).show()

        }
        binding.autoPlayVideo.setOnClickListener { v ->
            val isChecked = (v as SwitchMaterial).isChecked
            viewModel.setAutoPlayVideo(isChecked)
            Toast.makeText(
                this,
                if (isChecked)
                    "Se habilitó la reproducción automatica de video."
                else
                    "Se deshabilitó la reproducción automatica de video.",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    private fun createDialogChangeName(oldName: String) {
        BasicDialog(this)
            .setInputTypeText()
            .setText("Cambiar nombre")
            .setInputText(oldName)
            .requestFocus()
            .setButtonListener { dialog ->
                val name: String = dialog.getInput()
                if (name.length in 3..15) {
                    viewModel.setName(name)
                    viewModel.load()
                    Toast.makeText(
                        this,
                        "Genial tu nuevo nombre $name !!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        "Debes Ingresar un nombre válido, entre 3 y 15 caracteres.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.show()
    }

    private fun createDialogLimit(isStorage: Boolean) {
        val text: String =
            ("Establecer limite de " + (if (isStorage) "almacenamiento, " else "datos, ") + "en MB (MegaBytes).")
        BasicDialog(this)
            .setText(text)
            .setHintText("Límite de " + if (isStorage) "almacenamiento." else "datos.")
            .setInputTypeNumber()
            .setButtonListener { dialog ->
                val input = dialog.getInput()
                if (input.isNotBlank() && input.isDigitsOnly() && input.toInt() > 20) {
                    if (isStorage) {
                        viewModel.setLimitStorage(input.toInt())
                        Toast.makeText(
                            this,
                            "Ahora la aplicacion solo puede almacenar $input MB.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.setLimitMovil(input.toInt())
                        Toast.makeText(
                            this,
                            "Ahora la aplicacion solo puede descargar $input MB.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.load()
                    dialog.dismiss()
                } else
                    Toast.makeText(
                        this,
                        "$input debe ser un número mayor a 20 MB.",
                        Toast.LENGTH_SHORT
                    ).show()
            }.show()
    }

    private fun createDialogDelete() {
        BasicDialog(this)
            .setText("Estas a punto de borrar tu progreso. Deberás volver a comenzar.")
            .setButtonText("Borrar")
            .setButtonListener { dialog ->
                viewModel.clearData()
                viewModel.load()
                Toast.makeText(this, "Tu progreso volvió a 0", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }.show()
    }


    private fun createClearStorageDialog() {
        DialogClearStorage(this)
            .setOnDismissListener { viewModel.load() }
            .show()
    }
}