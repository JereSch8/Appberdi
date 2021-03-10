package com.jackemate.appberdi.ui.map

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMainBinding
import com.jackemate.appberdi.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<MapViewModel>()
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding  = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        viewModel.sites.observe(this) {
            it.forEach { site ->
                val geoPoint : GeoPoint = site.latlong as GeoPoint
                val landmark = LatLng(geoPoint.latitude, geoPoint.longitude)
                mMap.addMarker(MarkerOptions().position(landmark).title(site.name))
                mMap.setContentDescription(site.description)

                if(site.name.contains("Plaza Colón"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(landmark))
            }
        }
        // Actualizamos
        viewModel.getSites()

    }
}