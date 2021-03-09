package com.jackemate.appberdi

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jackemate.appberdi.ui.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Observamos el LiveData
        viewModel.sites.observe(this) {
            main_text.text = it.toString()
            it.forEach { site ->
                Log.e("SITE: ", "description: ${site.description}")
                Log.e("SITE: ", "latlon: ${site.latlon}")
                Log.e("SITE: ", "name: ${site.name}")
            }
        }
        // Actualizamos
        viewModel.getSites()

//Forma directa de acceder al repo y obtener lo que queremos, el tema de esto es que si
//        modificamos despues el metodo en el LocationRepository se ve afectado esto

//        LocationRepository(this).getAllSite().forEach { site ->
//            Log.e( "SITE: ", "description: ${site.description}" )
//            Log.e( "SITE: ", "latlon: ${site.latlon}" )
//            Log.e( "SITE: ", "name: ${site.name}" )
//        }

    }
}