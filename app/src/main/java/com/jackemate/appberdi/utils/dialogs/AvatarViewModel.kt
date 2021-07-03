package com.jackemate.appberdi.utils.dialogs

import androidx.lifecycle.ViewModel
import com.jackemate.appberdi.R
import com.jackemate.appberdi.domain.entities.Board

class AvatarViewModel : ViewModel() {
     fun getListAvatars() : List<Board>{
        return listOf(
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
    }
}

