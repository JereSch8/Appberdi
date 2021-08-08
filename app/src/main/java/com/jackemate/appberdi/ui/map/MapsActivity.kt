package com.jackemate.appberdi.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.jackemate.appberdi.ui.sites.SiteActivity
import com.jackemate.appberdi.utils.*
import kotlin.math.roundToInt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel by viewModels<MapViewModel>()
    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val loc = locationResult.locations.first()
            currentPos = LatLng(loc.latitude, loc.longitude)
            updateUI()
        }
    }

    private var currentPos: LatLng? = null
    private var currentSites: List<SiteMarker>? = null
    private lateinit var binding: ActivityMapsBinding

    private lateinit var polyline: Polyline
    private var status: TourMapStatus = TourMapStatus.Navigating

    sealed class TourMapStatus {
        object Navigating : TourMapStatus()
        class SiteSelected(val site: SiteMarker) : TourMapStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        initMap()
        initPolyline()

        observe(viewModel.sites) { sitesMarkers ->
            Log.d(TAG, "sitesMarkers: $sitesMarkers")
            currentSites = sitesMarkers
            initMarkers()
            updateUI()
        }
    }

    private fun initMarkers() {
        val sites = currentSites ?: return
        sites.forEach { site ->
            addSiteMarker(site)
        }
        moveCameraByBounds(sites)
    }

    private fun moveCameraByBounds(sites: List<SiteMarker>) {
        val bounds = getBoundsBy(sites)
        Log.d(TAG, "moveCamera: ${binding.map.width}")

        // En este punto es posible que el mapa sepa sus dimensiones
        // Por lo que se los tengo que pasar a manopla
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.map.width,
                binding.map.height,
                100
            )
        )
    }

    private fun getBoundsBy(sitesMarkers: List<SiteMarker>): LatLngBounds.Builder {
        val bounds = LatLngBounds.Builder()
        sitesMarkers.forEach {
            bounds.include(it.pos)
        }
        return bounds
    }

    private fun addSiteMarker(site: SiteMarker) {
        // https://github.com/googlemaps/android-maps-utils/blob/main/demo/src/gms/java/com/google/maps/android/utils/demo/IconGeneratorDemoActivity.java
        val iconFactory = IconGenerator(this)
        val marker = map.addMarker(
            MarkerOptions()
                .position(site.pos)
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        if (site.visited) BitmapDescriptorFactory.HUE_AZURE
                        else BitmapDescriptorFactory.HUE_RED
                    )
                )
        )
        marker.tag = site
        marker.showInfoWindow()
    }

    private fun initPolyline() {
        polyline = map.addPolyline(
            PolylineOptions()
                .color(Color.BLACK)
                .width(20f)
                .pattern(listOf(Dot(), Gap(15f)))
                .jointType(JointType.ROUND)
                .geodesic(false) // Lineas curvas o no por la proyección del planeta
        )
    }

    @SuppressLint("PotentialBehaviorOverride", "MissingPermission")
    private fun initMap() {
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false

        if (hasAnyPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            map.isMyLocationEnabled = true
        }
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener {
            status = TourMapStatus.Navigating
            updateUI()
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        viewModel.updateSites()
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
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    // El IDE no toma la extensión Context.hasPermission()
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (hasPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun distanceTo(latLng: LatLng): Double {
        if (currentPos == null) {
            return Double.MAX_VALUE
        }
        return SphericalUtil.computeDistanceBetween(latLng, currentPos)
    }

    private fun computeBestSite(): SiteMarker? {
        return currentSites?.let { sites ->
            sites
                .filter { !it.visited }
                .minByOrNull { distanceTo(it.pos) }
        }
    }

    private fun updateUI() {
        Log.d(TAG, "updateUI: currentPos: $currentPos")

        when (val stat = status) {
            is TourMapStatus.Navigating -> {

                // No hacer nada si no hay posición actual
                if (currentPos == null) {
                    binding.tvNextStop.text = ""
                    binding.tvNameSite.text = "Pensando..."
                    binding.tvDistance.text = ""
                    polyline.points = emptyList()
                    binding.btnAccessible.visible(false)
                    binding.btnEnter.hide()
                    return
                }

                val best = computeBestSite()

                Log.d(TAG, "best: $best")
                best?.pos?.let {
                    val distance = distanceTo(best.pos).roundToInt()
                    binding.tvNextStop.text = "Sitio más cercano:"
                    binding.tvNameSite.text = best.title
                    binding.tvDistance.text = "Estás masomenos a $distance metros"
                    polyline.points = listOf(currentPos, best.pos)
                    binding.btnAccessible.visible(best.accessible)
                }

                if (best != null && distanceTo(best.pos) < Constants.GEOFENCE_RADIUS_IN_METERS) {
                    binding.btnEnter.show()
                } else {
                    binding.btnEnter.hide()
                }
            }
            is TourMapStatus.SiteSelected -> {
                binding.tvNextStop.text =
                    if (stat.site.visited) "Ya lo visitaste"
                    else "Ir a:"
                binding.tvNameSite.text = stat.site.title

                if (currentPos != null) {
                    val distance = distanceTo(stat.site.pos).roundToInt()
                    binding.tvDistance.text = "Estás a $distance metros masomono."
                    polyline.points = listOf(currentPos, stat.site.pos)
                }

                binding.btnEnter.show()
                binding.btnAccessible.visible(stat.site.accessible)
            }
        }
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
        val intent = Intent(this, SiteActivity::class.java)
        intent.putExtra("idSite", id)
        startActivity(intent)
    }
}