package com.jackemate.appberdi.ui.map

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMapsBinding
import com.jackemate.appberdi.ui.sites.ContentSite

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel by viewModels<MapViewModel>()
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding  = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cvHeader.setCardBackgroundColor(Color.TRANSPARENT)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.btnIndications.setOnClickListener{
            Log.e("onCreate: ", "PRESS")
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false

        viewModel.sites.observe(this) {
            it.forEach { site ->
                val geoPoint : GeoPoint = site.latlong as GeoPoint
                val landmark = LatLng(geoPoint.latitude, geoPoint.longitude)
                mMap.addMarker(MarkerOptions().position(landmark).title(site.name))
                mMap.setContentDescription(site.description)
                mMap.setMaxZoomPreference(100.0f)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(landmark, 14.5f))
            }
        }
        // Actualizamos
        viewModel.getSites()

        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(this, ContentSite::class.java)
        intent.putExtra("idSite", marker.title)
        startActivity(intent)
        return false
    }
}