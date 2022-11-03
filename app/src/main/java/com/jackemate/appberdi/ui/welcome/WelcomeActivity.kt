package com.jackemate.appberdi.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.databinding.ActivityWelcomeBinding
import com.jackemate.appberdi.entities.Board
import com.jackemate.appberdi.ui.main.MainActivity
import com.jackemate.appberdi.ui.shared.dialogs.BasicDialog
import com.jackemate.appberdi.utils.transparentStatusBar

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val preferenceRepo by lazy { PreferenceRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        transparentStatusBar()

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPageAdapter(boards, this::onClickListener)

        Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN) {}
    }

    private fun onClickListener(position: Int) {
        if (position == (boards.size - 1)) {

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE) {}

            BasicDialog(this)
                .setButtonEnabled(false)
                .setInputTypeText()
                .setTitle(getString(R.string.como_te_llamas))
                .setButtonEnabled(true)
                .setButtonListener { dialog ->
                    val name: String = dialog.getInput().trim().ifEmpty { "Firulais" }
                    preferenceRepo.setUserName(name)
                    preferenceRepo.setFirstUsage()
                    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                        param(FirebaseAnalytics.Param.VALUE, name)
                    }
                    dialog.cancel()
                    goToMain()
                }
                .show()
        } else {
            binding.viewPager.setCurrentItem((position + 1), true)
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
        })
    }

    private val boards = listOf(
        Board(
            background = R.drawable.background,
            animation = R.raw.neighborhood,
            title = "Bienvenida/o al barrio Alberdi",
            description = "Estás a punto de comenzar un gran viaje a través de barrio Alberdi."
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.tour_walking,
            title = "Recorrido Presencial",
            description = "Te guiaremos durante tu visita para que aproveches al máximo tu recorrido."
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.tour_virtual,
            title = "Recorrido Virtual",
            description = "Si no podés visitar el barrio no te preocupes: podés hacerlo desde donde estés."
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.girl_mediateca,
            title = "Mediateca",
            description = "Acceso a increíbles documentos del barrio Alberdi"
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.attraction,
            title = "Actividades",
            description = "Descubrí todas las actividades culturales que tiene el barrio para vos."
        )
    )

}