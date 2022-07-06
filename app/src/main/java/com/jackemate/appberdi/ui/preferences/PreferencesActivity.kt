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

                setLimitStorage.text = getString(R.string.limite_almacenamiento, data.storageLimit)
                setLimitMovil.text = getString(R.string.limite_datos, data.mobilLimit)
                cleanStorage.text = getString(R.string.borrar_datos_almacenados, data.storageSize)

                progressSite.apply {
                    setProgressWithAnimation(data.siteProgress, 3000) // =3s
                    progressMax = data.siteTotal
                    binding.txtProgressSite.text = getString(
                        R.string.progress_site,
                        data.siteProgress.toInt(),
                        progressMax.toInt()
                    )

                }

                progressTreasure.apply {
                    setProgressWithAnimation(data.treasureProgress, 3000) // =3s
                    progressMax = data.treasureTotal
                    binding.txtProgressTreasure.text = getString(
                        R.string.progress_site,
                        data.treasureProgress.toInt(),
                        progressMax.toInt()
                    )
                }

                autoPlayAudio.isChecked = data.autoPlayAudio
                autoPlayVideo.isChecked = data.autoPlayVideo
            }

            binding.nameUser.setOnClickListener { createDialogChangeName(data.username) }
        }

        binding.avatarUser.setOnClickListener {
            DialogAvatars(this)
                .setText(getString(R.string.selecciona_avatar))
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
                if (isChecked) getString(R.string.audio_habilitado)
                else getString(R.string.audio_deshabilitado),
                Toast.LENGTH_SHORT
            ).show()

        }
        binding.autoPlayVideo.setOnClickListener { v ->
            val isChecked = (v as SwitchMaterial).isChecked
            viewModel.setAutoPlayVideo(isChecked)
            Toast.makeText(
                this,
                if (isChecked) getString(R.string.video_habilitado)
                else getString(R.string.video_deshabilitado),
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
            .setText(getString(R.string.cambiar_nombre))
            .setInputText(oldName)
            .requestFocus()
            .setButtonListener { dialog ->
                val name: String = dialog.getInput()
                if (name.length in 3..15) {
                    viewModel.setName(name)
                    viewModel.load()
                    Toast.makeText(
                        this,
                        getString(R.string.nuevo_nombre, name),
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.debes_ingresar),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.show()
    }

    private fun createDialogLimit(isStorage: Boolean) {
        val text: String = getString(
            R.string.establecer_limite,
            (if (isStorage) "almacenamiento, " else "datos, ")
        )
        BasicDialog(this)
            .setText(text)
            .setHintText( getString(
                    R.string.limite_en,
                    ( if (isStorage) "almacenamiento." else "datos." )
                )
            )
            .setInputTypeNumber()
            .setButtonListener { dialog ->
                val input = dialog.getInput()
                if (input.isNotBlank() && input.isDigitsOnly() && input.toInt() > 20) {
                    if (isStorage) {
                        viewModel.setLimitStorage(input.toInt())
                        Toast.makeText(
                            this,
                            getString(R.string.ahora_la_app_almacena, input),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.setLimitMovil(input.toInt())
                        Toast.makeText(
                            this,
                            getString(R.string.ahora_la_app_descarga, input),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.load()
                    dialog.dismiss()
                } else
                    Toast.makeText(
                        this,
                        getString(R.string.debe_ser_mayor, input),
                        Toast.LENGTH_SHORT
                    ).show()
            }.show()
    }

    private fun createDialogDelete() {
        BasicDialog(this)
            .setText(getString(R.string.borrar_progreso))
            .setButtonText(getString(R.string.borrar))
            .setButtonListener { dialog ->
                viewModel.clearData()
                viewModel.load()
                Toast.makeText(this, getString(R.string.progress_reset), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }.show()
    }


    private fun createClearStorageDialog() {
        DialogClearStorage(this)
            .setOnDismissListener { viewModel.load() }
            .show()
    }
}