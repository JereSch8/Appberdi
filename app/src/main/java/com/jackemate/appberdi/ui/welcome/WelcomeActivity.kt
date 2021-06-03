package com.jackemate.appberdi.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityWelcomeBinding
import com.jackemate.appberdi.ui.main.MainActivity
import com.jackemate.appberdi.utils.DialogCustom
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.onTextChanged
import com.jackemate.appberdi.utils.visible

class WelcomeActivity : AppCompatActivity(), ViewPageAdapter.OnItemSelected {
    private lateinit var viewModel: WelcomeViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Welcome)
        super.onCreate(savedInstanceState)

        if (LocalInfo(this).isntFirstUsage()) goMain()

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)

        val adapter = ViewPageAdapter(viewModel.getListBoard(), this)
        viewPager = binding.viewPager

        viewPager.adapter = adapter
    }

    override fun onClickListener(position: Int) {
        if (position == (viewModel.getListBoard().size - 1)) {
            val dialog = DialogCustom(this, this)

            dialog.setText("¿Cómo querés que te llamemos?")
            dialog.getEditText()?.onTextChanged {
                dialog.getSave().visible(it.length in 3..15)
                dialog.getSave().isEnabled = it.length in 3..15
            }

            dialog.getSave().setOnClickListener {
                val name: String = dialog.getInput()
                LocalInfo(this).setUserName(name)
                LocalInfo(this).setFirstUsage()
                dialog.cancel()
                goMain()
            }
            dialog.setAnimation(R.raw.astronaut_dog)
            dialog.getSave().visible(false)
            dialog.make()
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