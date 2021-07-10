package com.jackemate.appberdi.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.databinding.ActivityWelcomeBinding
import com.jackemate.appberdi.ui.main.MainActivity
import com.jackemate.appberdi.ui.shared.dialogs.BasicDialog
import com.jackemate.appberdi.utils.transparentStatusBar

class WelcomeActivity : AppCompatActivity(), ViewPageAdapter.OnItemSelected {
    private lateinit var viewModel: WelcomeViewModel
    private lateinit var viewPager: ViewPager2
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

        val adapter = ViewPageAdapter(viewModel.getListBoard(), this)
        viewPager = binding.viewPager

        viewPager.adapter = adapter
    }

    override fun onClickListener(position: Int) {
        if (position == (viewModel.getListBoard().size - 1)) {
            BasicDialog(this)
                .setButtonEnabled(false)
                .setInputTypeText()
                .setText("¿Cómo querés que te llamemos?")
                .setInputListener { dialog, input ->
                    dialog.setButtonEnabled(input.length in 3..15)
                }
                .setButtonListener { dialog ->
                    val name: String = dialog.getInput()
                    PreferenceRepository(this).setUserName(name)
                    PreferenceRepository(this).setFirstUsage()
                    dialog.cancel()
                    goMain()
                }
                .show()
        } else
            viewPager.setCurrentItem((position + 1), true)
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

}