package com.jackemate.appberdi.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.databinding.ActivityWelcomeBinding
import com.jackemate.appberdi.ui.main.MainActivity
import com.jackemate.appberdi.ui.shared.dialogs.BasicDialog
import com.jackemate.appberdi.utils.transparentStatusBar

class WelcomeActivity : AppCompatActivity() {
    private lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        transparentStatusBar()

        val limitStorage = PreferenceRepository(this).getLimitStorage()
        val limitMovil = PreferenceRepository(this).getLimitMovil()
        if (limitStorage == -8)
            PreferenceRepository(this).setLimitStorage(150)
        if (limitMovil != -8)
            PreferenceRepository(this).setLimitMovil(150)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)

        val adapter = ViewPageAdapter(viewModel.getListBoard(), this::onClickListener)

        binding.viewPager.adapter = adapter
    }

    private fun onClickListener(position: Int) {
        if (position == (viewModel.getListBoard().size - 1)) {
            BasicDialog(this)
                .setButtonEnabled(false)
                .setInputTypeText()
                .setText("¿Cómo querés que te llamemos?")
                .setInputListener { dialog, input ->
                    dialog.setButtonEnabled(input.trim().length in 1..15)
                }
                .setButtonListener { dialog ->
                    val name: String = dialog.getInput().trim()
                    PreferenceRepository(this).setUserName(name)
                    PreferenceRepository(this).setFirstUsage()
                    dialog.cancel()
                    goToMain()
                }
                .show()
        } else
            binding.viewPager.setCurrentItem((position + 1), true)
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

}