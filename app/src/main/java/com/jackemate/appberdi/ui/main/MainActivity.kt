package com.jackemate.appberdi.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.databinding.ActivityMainBinding
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.services.GeofenceBroadcastReceiver
import com.jackemate.appberdi.services.TrackingService
import com.jackemate.appberdi.ui.about.AboutActivity
import com.jackemate.appberdi.ui.cultural.CulturalActivity
import com.jackemate.appberdi.ui.map.MapsActivity
import com.jackemate.appberdi.ui.mediateca.MediatecaStart
import com.jackemate.appberdi.ui.preferences.PreferencesActivity
import com.jackemate.appberdi.ui.shared.RequesterPermissionsActivity
import com.jackemate.appberdi.ui.welcome.WelcomeActivity
import com.jackemate.appberdi.utils.*


class MainActivity : RequesterPermissionsActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var geofencingClient: GeofencingClient
    private val preferenceRepo by lazy { PreferenceRepository(applicationContext) }
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (!preferenceRepo.isntFirstUsage()) {
            goWelcome()
            return
        }
        setContentView(binding.root)

        // Indispensable para el RequesterPermissionsActivity
        root = binding.root

        geofencingClient = LocationServices.getGeofencingClient(this)

        val name: String = preferenceRepo.getUserName()
        binding.msgWelcome.text = getString(R.string.welcome, name)

        // Pedimos el permiso de GPS
        withPermissions(gpsPermissions()) {
            Log.i(TAG, "onCreate: Traer sitios")
            observe(viewModel.getSites()) {
                Log.i(TAG, "onCreate: setupGeofence")
                setupGeofence(it)
            }
        }

        binding.launchTour.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.launchPreferences.setOnClickListener {
            startActivity(Intent(this, PreferencesActivity::class.java))
        }

        binding.launchAttractions.setOnClickListener {
            startActivity(Intent(this, CulturalActivity::class.java))
        }

        binding.launchMediateca.setOnClickListener {
            startActivity(Intent(this, MediatecaStart::class.java))
        }

        binding.launchAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val running = isServiceRunning(TrackingService::class.java)
        binding.launchTour.text = getString(
            if (running) R.string.continuar_recorrido
            else R.string.iniciar_recorrido
        )
    }

    @SuppressLint("MissingPermission")
    private fun setupGeofence(sites: List<Site>) {
        if (!hasPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            return
        }

        val geofenceList = sites.map {
            val geoPoint: GeoPoint = it.pos as GeoPoint
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

    private fun goWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        finish()
    }

    private fun getGeofencingRequest(list: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            addGeofences(list)
        }.build()
    }

    private fun gpsPermissions(): Array<String> = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    ).toTypedArray()
}