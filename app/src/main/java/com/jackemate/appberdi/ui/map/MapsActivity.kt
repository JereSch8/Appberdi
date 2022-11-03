package com.jackemate.appberdi.ui.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ActivityMapsBinding
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.services.AudioService
import com.jackemate.appberdi.services.TrackingService
import com.jackemate.appberdi.services.TrackingService.Companion.ACTION_FORCE
import com.jackemate.appberdi.services.TrackingService.Companion.EXTRA_SITE
import com.jackemate.appberdi.ui.sites.SiteActivity
import com.jackemate.appberdi.utils.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<MapViewModel>()
    private lateinit var binding: ActivityMapsBinding

    private lateinit var map: GoogleMap
    private var polyline: Polyline? = null

    private var markers = emptyMap<String, Marker>()
    private var lastPos: LatLng? = null

    private val markerIconDefault by lazy { BitmapDescriptorFactory.defaultMarker(210f) }
    private val markerIconVisited by lazy { BitmapDescriptorFactory.defaultMarker(59f) }

    private val trackingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, intent.pretty())

            lastPos = intent.getParcelableExtra("pos")
            val mode = intent.getSerializableExtra("mode") as TourMode

            viewModel.setMode(mode)
        }
    }

    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnOptions.setOnClickListener {
            createDialogOptions()
        }

        observe(viewModel.mode, ::updateUI)
        viewModel.setMode(TourMode.Thinking)
    }

    private fun createDialogOptions() {
        DialogOptions(this, viewModel.getVirtualMode())
            .setVirtualModeListener { isVirtual ->
                viewModel.setVirtualMode(isVirtual)
                if (isVirtual) {
                    stopServices()
                    viewModel.setMode(TourMode.Thinking)
                } else {
                    startTracking()
                }
            }
            .setStopButtonListener {
                stopServices()
                finish()
            }
            .show()
    }

    private fun stopServices() {
        stopService(Intent(this, AudioService::class.java))
        stopService(Intent(this, TrackingService::class.java))
        lastPos = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateSites()
        if (!viewModel.getVirtualMode()) {
            startTracking()
        }
    }

    private fun startTracking() {
        registerReceiver(trackingReceiver, IntentFilter(TrackingService.TRACKING_UPDATES))
        registerReceiver(stopReceiver, IntentFilter(TrackingService.TRACKING_STOP))

        ContextCompat.startForegroundService(this,
            Intent(this, TrackingService::class.java)
                .apply { action = ACTION_FORCE }
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        try {
            unregisterReceiver(trackingReceiver)
            unregisterReceiver(stopReceiver)
        } catch (_: Exception) {
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "Map Ready!")
        map = googleMap
        initMap()
        Log.i(TAG, "initMap Ready!")
        initPolyline()
        Log.i(TAG, "initPolyline Ready!")

        observe(viewModel.sites) { sitesMarkers ->
            if (sitesMarkers == null) return@observe finishWithToast()
            Log.i(TAG, "sitesMarkers: ${sitesMarkers.size}")
            rebuildMarkers(sitesMarkers)
            viewModel.triggerModeUpdate()
        }
    }

    // https://github.com/googlemaps/android-maps-utils/blob/main/demo/src/gms/java/com/google/maps/android/utils/demo/IconGeneratorDemoActivity.java
    // val iconFactory = IconGenerator(this)
    private fun rebuildMarkers(sites: List<SiteMarker>) {
        if (markers.isEmpty()) {
            moveCameraByBounds(sites)
        }
        markers.values.forEach { it.remove() }
        markers = sites.map { site -> Pair(site.id, buildMarker(site)) }.toMap()
    }

    private fun buildMarker(site: SiteMarker): Marker {
        return map.addMarker(
            MarkerOptions()
                .position(site.pos.toLatLng())
                .icon(if (site.visited) markerIconVisited else markerIconDefault)
        )!!.apply {
            tag = site
        }
    }

    private fun moveCameraByBounds(sites: List<SiteMarker>) {
        if (sites.isEmpty()) {
            return
        }

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
        return sitesMarkers.fold(LatLngBounds.Builder()) { builder, site ->
            builder.include(site.pos.toLatLng())
        }
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

        if (hasAnyPermission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
            map.isMyLocationEnabled = true
        }
        map.setOnMarkerClickListener(::onMarkerClick)
        map.setOnMapClickListener { toNavigate() }
    }

    private fun onMarkerClick(marker: Marker): Boolean {
        val site = marker.tag as SiteMarker
        Log.i(TAG, "onMarkerClick: site: ${site.title}")

        if (viewModel.getVirtualMode()) {
            viewModel.setMode(TourMode.Ready(site, null))
        } else {
            val intent = Intent(this, TrackingService::class.java)
            intent.action = TrackingService.ACTION_SELECT
            intent.putExtra(EXTRA_SITE, site.id)
            ContextCompat.startForegroundService(this, intent)
        }

        return false
    }

    private fun toNavigate() {
        if (viewModel.getVirtualMode()) {
            viewModel.setMode(TourMode.Thinking)
        } else {
            val intent = Intent(this, TrackingService::class.java)
            intent.action = TrackingService.ACTION_NAVIGATE
            ContextCompat.startForegroundService(this, intent)
        }
    }

    private fun updateUI(mode: TourMode) {
        Log.d(TAG, "updateUI")
        val currentPos = lastPos

        when (mode) {
            is TourMode.Thinking -> {
                binding.tvNameSite.text = getString(
                    if (viewModel.getVirtualMode()) R.string.pensando_virtual
                    else R.string.pensando
                )
                binding.tvNextStop.text = ""
                binding.tvDistance.text = ""
                polyline?.points = emptyList()
                binding.btnAccessible.visible(false)
                binding.btnEnter.hide()
                setMarkerFocus(null)
            }
            is TourMode.Navigating -> {
                binding.tvNextStop.text = getString(R.string.sitio_mas_cercano)
                binding.tvNameSite.text = mode.best.title
                binding.tvDistance.text = getString(R.string.estas_a, mode.distance)
                binding.btnAccessible.visible(mode.best.accessible)

                if (currentPos != null) {
                    polyline?.points = listOf(currentPos, mode.best.pos.toLatLng())
                }

                binding.btnEnter.hide()
                binding.btnEnter.setOnClickListener(null)
                setMarkerFocus(null)
            }
            is TourMode.Selected -> {
                binding.tvNextStop.text =
                    if (mode.site.visited) "Ya lo visitaste"
                    else "Ir a:"
                binding.tvNameSite.text = mode.site.title
                binding.btnAccessible.visible(mode.site.accessible)

                if (currentPos != null) {
                    binding.tvDistance.text = getString(R.string.estas_a, mode.distance)
                    polyline?.points = listOf(currentPos, mode.site.pos.toLatLng())
                }

                binding.btnEnter.hide()
                binding.btnEnter.setOnClickListener(null)

                setMarkerFocus(markers[mode.site.id])
            }
            is TourMode.Ready -> {
                binding.tvNextStop.text =
                    if (mode.site.visited) "Estás de nuevo en"
                    else "Estás en"
                binding.tvNameSite.text = mode.site.title
                binding.tvDistance.text = "Ya podés iniciar el recorrido!"
                binding.btnAccessible.visible(mode.site.accessible)

                if (currentPos != null) {
                    polyline?.points = listOf(currentPos, mode.site.pos.toLatLng())
                }

                binding.btnEnter.show()
                binding.btnEnter.setOnClickListener { openSite(mode.site.id) }

                setMarkerFocus(markers[mode.site.id])
            }
        }
    }

    private fun setMarkerFocus(marker: Marker?) {
//        markers.values.forEach { it.alpha = if (marker == null || it == marker) 1f else 0.5f }
        markers.values.forEach {
//            it.setIcon(
//                if ((it.tag as SiteMarker).visited) {
//                    if (marker == it) markerIconVisitedFocus else markerIconVisited
//                } else {
//                    if (marker == it) markerIconFocus else markerIconDefault
//                }
//            )
            it.alpha = when {
                marker == null -> .8f
                it == marker -> 1f
                else -> .2f
            }
        }
    }

    private fun openSite(id: String) {
        Log.i(TAG, "openSite: $id")
        val intent = Intent(this, SiteActivity::class.java)
        intent.putExtra("idSite", id)
        startActivity(intent)
    }
}