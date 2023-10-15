package com.jackemate.appberdi.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest.Builder
import com.google.android.gms.location.LocationServices
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.databinding.ActivityMainBinding
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.services.GeofenceReceiver
import com.jackemate.appberdi.services.TrackingService
import com.jackemate.appberdi.ui.about.AboutActivity
import com.jackemate.appberdi.ui.cultural.CulturalActivity
import com.jackemate.appberdi.ui.map.MapsActivity
import com.jackemate.appberdi.ui.mediateca.MediatecaStart
import com.jackemate.appberdi.ui.preferences.PreferencesActivity
import com.jackemate.appberdi.ui.shared.RequesterPermissionsActivity
import com.jackemate.appberdi.ui.welcome.WelcomeActivity
import com.jackemate.appberdi.utils.*
import java.util.concurrent.TimeUnit


class MainActivity : RequesterPermissionsActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private val preferenceRepo by lazy { PreferenceRepository(applicationContext) }

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

        val name: String = preferenceRepo.getUserName()
        binding.msgWelcome.text = getString(R.string.welcome, name)

        // Pedimos el permiso de GPS
        withPermissions(gpsPermissions()) {
            Log.i(TAG, "onCreate: Traer sitios")
            observe(viewModel.getSites()) {
                Log.i(TAG, "onCreate: setupGeofence: ${it?.size}")
                it?.let { setupGeofence(it) }
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

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    private fun setupGeofence(sites: List<Site>) {
        if (!hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        }

        val request = Builder()
            .addGeofences(sites.map { geofence(it) })
            .build()

        val intent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Log.i(TAG, "setupGeofence: Adding Geofences")
        LocationServices
            .getGeofencingClient(this)
            .addGeofences(request, intent)
            .run {
                addOnSuccessListener {
                    Log.i(TAG, "setupGeofence: Geofences added")
                }
                addOnFailureListener {
                    Log.e(TAG, "setupGeofence: Geofences NO", it)
                    Firebase.crashlytics.recordException(it)
                }
            }
    }

    private fun geofence(site: Site): Geofence {
        val geoPoint: GeoPoint = site.pos as GeoPoint
        return Geofence.Builder()

            // Set the request ID of the geofence. This is a string to identify this geofence.
            .setRequestId(site.id)

            // Set the circular region of this geofence.
            .setCircularRegion(
                geoPoint.latitude,
                geoPoint.longitude,
                500f // radius in meters
            )

            // Set the expiration duration of the geofence.
            .setExpirationDuration(TimeUnit.DAYS.toMillis(30))

            // Set the transition types of interest.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
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

    private fun gpsPermissions(): Array<String> = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ).toTypedArray()
}