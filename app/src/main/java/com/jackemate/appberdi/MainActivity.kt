package com.jackemate.appberdi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.jackemate.appberdi.data.LocationRepository
import com.jackemate.appberdi.data.RepoImplement
import com.jackemate.appberdi.domain.entities.Site

import com.jackemate.appberdi.ui.viewModels.MainActivityViewModel
import com.jackemate.appberdi.ui.viewModels.VMFactoryRepo

class MainActivity : AppCompatActivity() {

    //Le inyectamos las dependecias al viewmodel
    private val viewModel by viewModels<MainActivityViewModel> {
        VMFactoryRepo(RepoImplement(LocationRepository(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//Obtener la data a traves del livedata del viewModel

        viewModel.requestAllSite.observeForever {
            it.forEach { site ->
                Log.e( "SITE: ", "description: ${site.description}" )
                Log.e( "SITE: ", "latlon: ${site.latlon}" )
                Log.e( "SITE: ", "name: ${site.name}" )
            }
        }

//Forma directa de acceder al repo y obtener lo que queremos, el tema de esto es que si
//        modificamos despues el metodo en el LocationRepository se ve afectado esto

//        LocationRepository(this).getAllSite().forEach { site ->
//            Log.e( "SITE: ", "description: ${site.description}" )
//            Log.e( "SITE: ", "latlon: ${site.latlon}" )
//            Log.e( "SITE: ", "name: ${site.name}" )
//        }

    }
}