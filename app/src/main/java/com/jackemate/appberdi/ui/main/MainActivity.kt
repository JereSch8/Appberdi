package com.jackemate.appberdi.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.databinding.ActivityMainBinding
import com.jackemate.appberdi.ui.map.MapsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}