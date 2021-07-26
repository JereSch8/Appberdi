package com.jackemate.appberdi.ui.shared.dialogs.avatar

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogAvatarsBinding
import com.jackemate.appberdi.entities.Board
import com.jackemate.appberdi.ui.shared.DialogBuilder

class DialogAvatars(activity: Activity): DialogBuilder(activity) {
    override val binding = DialogAvatarsBinding.inflate(inflater)

    private val avatars = listOf(
        Board(
            background = R.drawable.background,
            animation = R.raw.astronaut_dog,
            title = "Albi Astronauta",
            description = "Albi cuando decide subirse a la nave y salir del barrio."
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.smiling_dog,
            title = "Albi Feliz",
            description = "Albi encontró un huesito en la calle y está muy feliz!!"
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.unicorn_dog,
            title = "Albi Unicornio",
            description = "Albi se comio un honguito y está alucinando ser unicornio."
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.flirting_dog,
            title = "Albi Canchero",
            description = "Albi volvió de la casa de Jardin Florido con piropos bajo la pata."
        )
    )

    init {
        binding.recycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recycler.adapter = AvatarListAdapter(
            avatars,
            ::onSelect
        )
    }

    fun setText(text: String) : DialogAvatars {
        binding.title.text = text
        return this
    }

    private fun onSelect(item: Board) {
        preferenceRepo.setAvatar(item.animation)
        setAnimation(item.animation)
        dismiss()
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}