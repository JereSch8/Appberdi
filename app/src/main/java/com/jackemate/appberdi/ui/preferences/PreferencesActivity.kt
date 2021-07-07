package com.jackemate.appberdi.ui.preferences

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.jackemate.appberdi.databinding.ActivityPreferencesBinding
import com.jackemate.appberdi.ui.shared.dialogs.BasicDialog
import com.jackemate.appberdi.ui.shared.dialogs.DialogClearStorage
import com.jackemate.appberdi.ui.shared.dialogs.avatar.DialogAvatars
import com.jackemate.appberdi.utils.toRoundString
import com.jackemate.appberdi.utils.transparentStatusBar

class PreferencesActivity : AppCompatActivity() {
    private val viewModel: PreferencesViewModel by viewModels()
    private lateinit var binding: ActivityPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameUser.text = viewModel.getName()

        if (viewModel.getLimitStorage() != -8)
            binding.setLimitStorage.text =
                "Límite de almacenamiento: ${viewModel.getLimitStorage()} MB."
        if (viewModel.getLimitMovil() != -8)
            binding.setLimitMovil.text = "Límite de datos: ${viewModel.getLimitMovil()} MB."

        if (viewModel.getAvatarResource() != -8)
            binding.avatarUser.setAnimation(viewModel.getAvatarResource())

        binding.avatarUser.setOnClickListener {
            DialogAvatars(this)
                .setText("Selecciona el avatar que más te guste!")
                .setOnDismissListener {
                    binding.avatarUser.setAnimation(viewModel.getAvatarResource())
                    binding.avatarUser.playAnimation()
                }
                .show()
        }

        binding.cleanStorage.text = "Borrar datos almacenados: ${viewModel.getSizeStorage()} MB."

        binding.progressSite.apply {
            setProgressWithAnimation(viewModel.getProgressSite(), 3000) // =3s
            progressMax = viewModel.getAmountSites()
            binding.txtProgressSite.text =
                "${viewModel.getProgressSite().toInt()}/${progressMax.toInt()}"
        }

        binding.progressTreasure.apply {
            setProgressWithAnimation(viewModel.getProgressTreasure(), 3000) // =3s
            progressMax = viewModel.getAmountTreasure()
            binding.txtProgressTreasure.text =
                "${viewModel.getProgressTreasure().toInt()}/${progressMax.toInt()}"
        }

        binding.nameUser.setOnClickListener { createDialogChangeName(binding) }

        binding.setLimitStorage.setOnClickListener { createDialogLimit(binding, true) }

        binding.cleanStorage.setOnClickListener { createClearStorageDialog(binding) }

        binding.setLimitMovil.setOnClickListener { createDialogLimit(binding, false) }

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

    private fun createDialogChangeName(binding: ActivityPreferencesBinding) {
        BasicDialog(this)
            .setInputTypeText()
            .setText("Cambiar nombre")
            .setSaveListener { dialog ->
                val name: String = dialog.getInput()
                if (name.length in 3..15) {
                    viewModel.setName(name)
                    binding.nameUser.text = name
                    Toast.makeText(this, "Genial tu nuevo nombre $name !!!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.cancel()
                } else
                    Toast.makeText(
                        this,
                        "Debes Ingresar un nombre válido, entre 3 y 15 caracteres.",
                        Toast.LENGTH_SHORT
                    ).show()
            }
            .show()
    }

    private fun createDialogLimit(binding: ActivityPreferencesBinding, isStorage: Boolean) {
        val text: String =
            ("Establecer limite de " + (if (isStorage) "almacenamiento, " else "datos, ") + "en MB (MegaBytes).")
        BasicDialog(this)
            .setText(text)
            .setHintText("Límite de " + if (isStorage) "almacenamiento." else "datos.")
            .setInputTypeNumber()
            .setSaveListener { dialog ->
                val limit = if (dialog.getInput().isNotBlank()) dialog.getInput() else " "
                if (limit.isDigitsOnly() && limit.toInt() > 20) {
                    if (isStorage) {
                        viewModel.setLimitStorage(limit.toInt())
                        binding.setLimitStorage.text = "Límite de almacenamiento: $limit MB."
                        Toast.makeText(
                            this,
                            "Ahora la aplicacion solo puede almacenar $limit MB.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.setLimitMovil(limit.toInt())
                        binding.setLimitMovil.text = "Límite de datos: $limit MB."
                        Toast.makeText(
                            this,
                            "Ahora la aplicacion solo puede descargar $limit MB.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dialog.cancel()
                } else
                    Toast.makeText(
                        this,
                        "$limit debe ser un número mayor a 20 MB.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
            }
            .show()
    }

    private fun createDialogDelete(binding: ActivityPreferencesBinding) {
        BasicDialog(this)
            .setText("Estas a punto de borrar tu progreso. Deberás volver a comenzar.")
            .setSaveListener { dialog ->
                viewModel.setProgressSite(-8)
                binding.progressSite.apply {
                    setProgressWithAnimation(0.1f, 3000) // =3s
                    progressMax = viewModel.getAmountSites()
                    binding.txtProgressSite.text = "0/${progressMax.toInt()}"
                }
                Toast.makeText(this, "Tu progreso volvió a 0", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
            .show()
    }


    private fun createClearStorageDialog(binding: ActivityPreferencesBinding) {
        DialogClearStorage(this)
            .setOnDismissListener {
                binding.cleanStorage.text =
                    "Borrar datos almacenados: ${viewModel.getSizeStorage().toRoundString()} MB."
            }
            .show()
    }
}