package com.jackemate.appberdi.ui.welcome

import androidx.lifecycle.ViewModel
import com.jackemate.appberdi.R
import com.jackemate.appberdi.domain.entities.Board

class WelcomeViewModel : ViewModel() {

     fun getListBoard() : List<Board>{
        return listOf(
            Board(
                background = R.drawable.gradient_background,
                animation = R.raw.neighborhood,
                title = "Bienvenida/o al barrio Alberdi",
                description = "Estas a punto de comenzar un gran viaje a través del barrio Alberdi."
            ),
            Board(
                background = R.drawable.gradient_background,
                animation = R.raw.tour_walking,
                title = "Recorrido Presencial",
                description = "Te guiaremos durante tu visita para que aproveches al maximo tu recorrido."
            ),
            Board(
                background = R.drawable.gradient_background,
                animation = R.raw.tour_virtual,
                title = "Recorrido Virtual",
                description = "Si no estás en el lugar, no te preocupes, tenemos un recorrido virtual para que lo conozcas."
            ),
            Board(
                background = R.drawable.gradient_background,
                animation = R.raw.girl_mediateca,
                title = "Mediateca",
                description = "Acceso a increibles documentos del barrio Alberdi!!!"
            ),
            Board(
                background = R.drawable.gradient_background,
                animation = R.raw.attraction,
                title = "Atracciones",
                description = "Descubrí todas las atracciones que tiene el barrio para vos."
            )
        )
    }


}

