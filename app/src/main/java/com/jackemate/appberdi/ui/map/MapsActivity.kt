package com.jackemate.appberdi.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ui.IconGenerator
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMapsBinding
import com.jackemate.appberdi.ui.sites.ContentSiteActivity
import com.jackemate.appberdi.utils.Constants
import com.jackemate.appberdi.utils.TAG
import kotlin.math.roundToInt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel by viewModels<MapViewModel>()
    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var currentPos: LatLng? = null
    private var currentSites: List<SiteMarker>? = null
    private lateinit var binding: ActivityMapsBinding

    private lateinit var polyline: Polyline
    private var status: TourMapStatus = TourMapStatus.Navigating

    sealed class TourMapStatus {
        object Navigating: TourMapStatus()
        class SiteSelected(val site: SiteMarker): TourMapStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val loc = locationResult.locations.first()
                currentPos = LatLng(loc.latitude, loc.longitude)
                updateUI()
            }
        }

        binding.btnEnter.setOnClickListener {
            Log.i(TAG, "btnEnter: $status")
            when (val stat = status) {
                is TourMapStatus.Navigating -> {
                    computeBestSite()?.let {
                        openSite(it.id)
                    }
                }
                is TourMapStatus.SiteSelected -> {
                    openSite(stat.site.id)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 20000
            // Es poco tiempo pero para las pruebas se ve bien
            // Sinó queda desfazada la linea con el puntito azul
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun distanceTo(latLng: LatLng): Double {
        return SphericalUtil.computeDistanceBetween(latLng, currentPos)
    }

    private fun computeBestSite(): SiteMarker? {
        // TODO filtar por visitados
        return currentSites?.let { site ->
            site.minByOrNull { distanceTo(it.pos) }
        }
    }

    private fun updateUI() {
        Log.d(TAG, "updateUI")

        Log.d(TAG, "currentSites: ${currentSites?.size}")
        Log.d(TAG, "currentLoc: $currentPos")

        when (val stat = status) {
            is TourMapStatus.Navigating -> {
                val best = computeBestSite()

                Log.d(TAG, "best: $best")
                best?.let {
                    val distance = distanceTo(best.pos).roundToInt()
                    binding.tvNextStop.text = "Sitio más cercano:"
                    binding.tvNameSite.text = best.title
                    binding.tvDistance.text = "Estás masomenos a $distance metros"
                    polyline.points = listOf(currentPos, best.pos)
                }

                if (best != null && distanceTo(best.pos) < Constants.GEOFENCE_RADIUS_IN_METERS) {
                    binding.btnEnter.show()
                } else {
                    binding.btnEnter.hide()
                }
            }
            is TourMapStatus.SiteSelected -> {
                val distance = distanceTo(stat.site.pos).roundToInt()
                binding.tvNextStop.text = "Ir a:"
                binding.tvNameSite.text = stat.site.title
                binding.tvDistance.text = "Estás a $distance metros masomono."
                polyline.points = listOf(currentPos, stat.site.pos)
                binding.btnEnter.show()
            }
        }

    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.isMyLocationEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            status = TourMapStatus.Navigating
            updateUI()
        }

        polyline = mMap.addPolyline(PolylineOptions().geodesic(true))

        viewModel.sites.observe(this) {
            currentSites = it
            it.forEach { site ->
//                https://github.com/googlemaps/android-maps-utils/blob/main/demo/src/gms/java/com/google/maps/android/utils/demo/IconGeneratorDemoActivity.java
                val iconFactory = IconGenerator(this)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(site.title)))
                        .position(site.pos)
                        .anchor(iconFactory.anchorU, iconFactory.anchorV)
                )
                marker.tag = site
                marker.showInfoWindow()
            }

            val bounds = it.fold(LatLngBounds.Builder(), { acc, site ->
                acc.include(site.pos)
            })
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
            updateUI()
        }

        viewModel.fetchSites()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val site = marker.tag as SiteMarker
        Log.i(TAG, "onMarkerClick: site: ${site.title}")
        status = TourMapStatus.SiteSelected(site)
        updateUI()
        return false
    }

    private fun openSite(id: String) {
        Log.i(TAG, "openSite: $id")
        val intent = Intent(this, ContentSiteActivity::class.java)
        intent.putExtra("idSite", id)
        startActivity(intent)
    }
}