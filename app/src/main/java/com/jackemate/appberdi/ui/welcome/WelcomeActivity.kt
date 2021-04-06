package com.jackemate.appberdi.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityWelcomeBinding
import com.jackemate.appberdi.ui.main.MainActivity
import com.jackemate.appberdi.utils.DialogCustom
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.TAG

class WelcomeActivity : AppCompatActivity(), ViewPageAdapter.OnItemSelected  {
    private lateinit var viewModel: WelcomeViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var binding : ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(LocalInfo(this).isntFirstUsage()) goMain()

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)

        val adapter = ViewPageAdapter(viewModel.getListBoard(), this)
        viewPager = binding.viewPager

        viewPager.adapter = adapter
    }

    override fun onClickListener(position: Int) {
        if (position == (viewModel.getListBoard().size -1)){

            LocalInfo(this).setFirstUsage()
            val dialog = DialogCustom(this, this)

            dialog.setText("Bienvenida/o a AppBerdi")
            dialog.getSave().setOnClickListener{
                val name : String = dialog.getInput()
                if(name.length in 4..15){
                    LocalInfo(this).setUserName(name)
                    Toast.makeText(this, "Bienvenida/o "+name, Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(this,"Debes Ingresar un nombre válido, entre 4 y 15 caracteres.", Toast.LENGTH_SHORT).show()

                dialog.cancel()
                goMain()
            }
            dialog.setAnimation(R.raw.astronaut_dog)
            dialog.make()
        }
        else
            viewPager.setCurrentItem((position + 1), true)
    }

    private fun goMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

}