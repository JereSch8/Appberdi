package com.jackemate.appberdi.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMapsBinding
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.services.AudioService
import com.jackemate.appberdi.services.TrackingService
import com.jackemate.appberdi.services.TrackingService.Companion.ACTION_FORCE
import com.jackemate.appberdi.services.TrackingService.Companion.EXTRA_SITE
import com.jackemate.appberdi.ui.sites.SiteActivity
import com.jackemate.appberdi.utils.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel by viewModels<MapViewModel>()
    private lateinit var map: GoogleMap

    private var currentSites: List<SiteMarker>? = null
    private lateinit var binding: ActivityMapsBinding

    private lateinit var polyline: Polyline
    private val receiver = TrackingBroadcastReceiver()

    inner class TrackingBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, intent.pretty())

            val pos = intent.getParcelableExtra<LatLng>("pos")
            val mode = intent.getSerializableExtra("mode") as TourMode

            updateUI(pos, mode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
//        transparentStatusBar()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.back.setOnClickListener {
            stopService(Intent(this, AudioService::class.java))
            stopService(Intent(this, TrackingService::class.java))
            finish()
        }

        ContextCompat.startForegroundService(this, Intent(this, AudioService::class.java))
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        viewModel.updateSites()
        registerReceiver(receiver, IntentFilter(TrackingService.TRACKING_UPDATES))

        ContextCompat.startForegroundService(this,
            Intent(this, TrackingService::class.java)
                .apply { action = ACTION_FORCE }
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        unregisterReceiver(receiver)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "Map Ready!")
        map = googleMap
        initMap()
        Log.i(TAG, "initMap Ready!")
        initPolyline()
        Log.i(TAG, "initPolyline Ready!")

        observe(viewModel.sites) { sitesMarkers ->
            Log.i(TAG, "sitesMarkers: ${sitesMarkers.size}")
            currentSites = sitesMarkers
            initMarkers()
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
        Log.d(TAG, "moveCamera: map size: ${binding.map.width}x${binding.map.height}")

        if (binding.map.width == 0 || binding.map.height == 0) {
            Log.w(TAG, "moveCamera: using screen size!")
        }

        val display = resources.displayMetrics
        val padding = display.widthPixels / 20

        // Cuando se abre la Activity, los datos pueden estar listos antes
        // de que tanto el SupportMapFragment o el FragmentContainerView NO sepan sus dimensiones.
        // Asumo que el MapFragment no lo va a saber directamente.
        // Por lo que intento tomando las dimensiones del View (usando binding)
        // y si no, uso directamente las dimensiones de la pantalla.
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.map.width.takeIf { it != 0 } ?: display.widthPixels,
                binding.map.height.takeIf { it != 0 } ?: display.heightPixels,
                padding
            )
        )
    }

    private fun getBoundsBy(sitesMarkers: List<SiteMarker>): LatLngBounds.Builder {
        val bounds = LatLngBounds.Builder()
        sitesMarkers.forEach {
            bounds.include(it.pos.toLatLng())
        }
        return bounds
    }

    private fun addSiteMarker(site: SiteMarker) {
        // https://github.com/googlemaps/android-maps-utils/blob/main/demo/src/gms/java/com/google/maps/android/utils/demo/IconGeneratorDemoActivity.java
        val iconFactory = IconGenerator(this)
        val marker = map.addMarker(
            MarkerOptions()
                .position(site.pos.toLatLng())
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        if (site.visited) BitmapDescriptorFactory.HUE_AZURE
                        else BitmapDescriptorFactory.HUE_RED
                    )
                )
        )
        marker?.tag = site
        marker?.showInfoWindow()
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
            val intent = Intent(this, TrackingService::class.java)
            intent.action = TrackingService.ACTION_NAVIGATE
            ContextCompat.startForegroundService(this, intent)
        }
    }

    private fun updateUI(currentPos: LatLng?, mode: TourMode) {
        Log.d(TAG, "updateUI")

        when (mode) {
            is TourMode.Thinking -> {
                binding.tvNextStop.text = ""
                binding.tvNameSite.text = "Pensando..."
                binding.tvDistance.text = ""
                polyline.points = emptyList()
                binding.btnAccessible.visible(false)
                binding.btnEnter.hide()
            }
            is TourMode.Navigating -> {
                binding.tvNextStop.text = "Sitio más cercano:"
                binding.tvNameSite.text = mode.best.title
                binding.tvDistance.text = "Estás masomenos a ${mode.distance} metros"
                binding.btnAccessible.visible(mode.best.accessible)
                polyline.points = listOf(currentPos, mode.best.pos.toLatLng())


                if (mode.distance < Constants.GEOFENCE_RADIUS_IN_METERS) {
                    binding.btnEnter.show()
                } else {
                    binding.btnEnter.hide()
                }

                binding.btnEnter.setOnClickListener {
                    Log.i(TAG, "btnEnter: $mode")
                    openSite(mode.best.id)
                }
            }
            is TourMode.Selected -> {
                binding.tvNextStop.text =
                    if (mode.site.visited) "Ya lo visitaste"
                    else "Ir a:"
                binding.tvNameSite.text = mode.site.title
                binding.btnAccessible.visible(mode.site.accessible)

                if (currentPos != null) {
                    binding.tvDistance.text = "Estás a ${mode.distance} metros masomono."
                    polyline.points = listOf(currentPos, mode.site.pos.toLatLng())
                }

                binding.btnEnter.show()
                binding.btnEnter.setOnClickListener {
                    Log.i(TAG, "btnEnter: $mode")
                    openSite(mode.site.id)
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val site = marker.tag as SiteMarker
        Log.i(TAG, "onMarkerClick: site: ${site.title}")

        val intent = Intent(this, TrackingService::class.java)
        intent.action = TrackingService.ACTION_SELECT
        intent.putExtra(EXTRA_SITE, site.id)
        ContextCompat.startForegroundService(this, intent)

        return false
    }

    private fun openSite(id: String) {
        Log.i(TAG, "openSite: $id")
        val intent = Intent(this, SiteActivity::class.java)
        intent.putExtra("idSite", id)
        startActivity(intent)
    }
}