package com.jackemate.appberdi.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.databinding.ActivityMainBinding
import com.jackemate.appberdi.domain.entities.Site
import com.jackemate.appberdi.services.GeofenceBroadcastReceiver
import com.jackemate.appberdi.ui.attractions.AttractionActivity
import com.jackemate.appberdi.ui.map.MapsActivity
import com.jackemate.appberdi.ui.mediateca.Mediateca
import com.jackemate.appberdi.utils.Constants
import com.jackemate.appberdi.utils.LocalInfo
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.observe

class MainActivity : RequesterPermissionsActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geofencingClient = LocationServices.getGeofencingClient(this)

        // Pedimos el permiso de GPS
        withPermissions(gpsPermissions()) {
            Log.i(TAG, "onCreate: Traer sitios")
            observe(viewModel.getSites()) {
                Log.i(TAG, "onCreate: setupGeofence")
                setupGeofence(it)
            }
            viewModel.getSites()
        }

        observe(viewModel.tourEvents()) {
            Log.e(TAG, "Hubo un cambio para el tour!!: $it")
        }

        binding.launchTour.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.launchSites.setOnClickListener {
        }

        binding.launchAttractions.setOnClickListener {
            startActivity(Intent(this, AttractionActivity::class.java))
        }

        binding.launchMediateca.setOnClickListener {
            startActivity(Intent(this, Mediateca::class.java))
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupGeofence(sites: List<Site>) {
        val geofenceList = sites.map {
            val geoPoint: GeoPoint = it.latlong as GeoPoint
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this geofence.
                .setRequestId(it.id)
                // Set the circular region of this geofence.
                .setCircularRegion(
                    geoPoint.latitude,
                    geoPoint.longitude,
                    Constants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest.
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build()
        }

        Log.i(TAG, "setupGeofence: Adding Geofences")
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent)
            .run {
                addOnSuccessListener {
                    Log.i(TAG, "setupGeofence: Geofences added")
                }
                addOnFailureListener {
                    it.printStackTrace()
                    Log.e(TAG, "setupGeofence: Geofences NO")
                }
            }
    }

    private fun getGeofencingRequest(list: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            addGeofences(list)
        }.build()
    }

    private fun gpsPermissions(): Array<String> = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            null
        }
    ).toTypedArray()
}