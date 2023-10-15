package com.jackemate.appberdi.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.toObject
import com.google.maps.android.SphericalUtil
import com.jackemate.appberdi.BuildConfig
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Position
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.ui.map.SiteMarker
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.hasPermission
import kotlin.math.roundToInt

class TrackingService : Service() {
    private val notifyRepo by lazy { NotifyRepository(this) }
    private val siteRepo by lazy { SiteRepository() }
    private val preferenceRepo by lazy { PreferenceRepository(this) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentPos: LatLng? = null
    private var currentSites: List<SiteMarker>? = null
    private var currentMode: TourMode = TourMode.Thinking

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val loc = locationResult.locations.firstOrNull() ?: return
            currentPos = LatLng(loc.latitude, loc.longitude)
            doUpdate()
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        startForeground(NotifyRepository.DEFAULT_ID, notifyRepo.foreground())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(NotifyRepository.DEFAULT_ID, notifyRepo.foreground())
        Log.d(TAG, "onStartCommand")

        initSites()
        when (intent.action) {
            ACTION_FORCE -> doUpdate()
            ACTION_SELECT -> doSelect(intent)
            ACTION_NAVIGATE -> doNavigate()
        }

        return START_REDELIVER_INTENT
    }

    override fun onLowMemory() {
        super.onLowMemory()
        stopLocationUpdates()
    }

    @Suppress("DEPRECATION")
    override fun onDestroy() {
        stopSelf()
        stopLocationUpdates()
        stopForeground(true)
        super.onDestroy()
    }

    private fun initSites() {
        siteRepo.getSites().get().addOnSuccessListener { snap ->
            val result = snap.documents
                .mapNotNull { it.toObject<Site>() }
                .map { SiteMarker(it, preferenceRepo.getDataLong(it.id)) }
            preferenceRepo.setAmountSites(result.size)
            preferenceRepo.setProgressSite(result.count { it.visited })
            currentSites = result
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
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun doSelect(intent: Intent) {
        val id = intent.getStringExtra(EXTRA_SITE)!!

        if (currentSites == null) {
            Log.w(TAG, "no select! currentSites is null")
            return
        }
        val site = currentSites!!.find { it.id == id }!!
        val distance = currentPos?.let { distanceTo(site.pos) }
        currentMode = if (isReady(site, distance)) {
            TourMode.Ready(site, site)
        } else {
            TourMode.Selected(site, distance?.roundToInt())
        }
        broadcast()
    }

    private fun doNavigate() {
        val best = computeBestSite()
        currentMode = if (best != null) {
            val distance = distanceTo(best.pos)
            if (isReady(best, distance)) {
                TourMode.Ready(best, null)
            } else {
                TourMode.Navigating(best, distance.roundToInt())
            }
        } else {
            TourMode.Thinking
        }
        broadcast()
    }

    private fun doUpdate() {
        val mode = currentMode

        Log.d(TAG, "doUpdate: currentPos: $currentPos, status: $mode")

        if (currentSites == null) {
            Log.w(TAG, "no update! currentSites is null")
            return
        }

        when (mode) {
            is TourMode.Thinking,
            is TourMode.Navigating -> {
                val best = computeBestSite()
                Log.d(TAG, "best: $best")

                if (best != null) {
                    val distance = distanceTo(best.pos)

                    currentMode = if (isReady(best, distance)) {
                        TourMode.Ready(best, null)
                    } else {
                        TourMode.Navigating(best, distance.roundToInt())
                    }

                    broadcast()
                }
            }
            is TourMode.Ready -> {
                val distance = distanceTo(mode.site.pos)
                currentMode = when {
                    isReady(mode.site, distance) -> mode // Noop
                    mode.selected != null -> TourMode.Selected(mode.selected, distance.roundToInt())
                    else -> TourMode.Thinking
                }

                broadcast()
            }
            is TourMode.Selected -> {
                val distance = currentPos?.let { distanceTo(mode.site.pos) }
                currentMode = if (isReady(mode.site, distance)) {
                    TourMode.Ready(mode.site, mode.site)
                } else {
                    TourMode.Selected(mode.site, distance?.roundToInt())
                }

                broadcast()
            }
        }
    }

    private fun broadcast() {
        val broadcast = Intent()
        broadcast.action = TRACKING_UPDATES
        broadcast.putExtra(EXTRA_UPDATE_POS, currentPos)
        broadcast.putExtra(EXTRA_UPDATE_MODE, currentMode)
        broadcast.setPackage(BuildConfig.APPLICATION_ID)
        sendBroadcast(broadcast)
    }

    private fun computeBestSite(): SiteMarker? {
        return currentSites?.let { sites ->
            sites
                .filter { !it.visited }
                .minByOrNull { distanceTo(it.pos) }
        }
    }

    private fun distanceTo(pos: Position): Double {
        if (currentPos == null) {
            return Double.MAX_VALUE
        }
        return SphericalUtil.computeDistanceBetween(pos.toLatLng(), currentPos)
    }

    private fun isReady(site: SiteMarker, distance: Double?): Boolean {
        if (distance == null) return false

        // Hardcode caso especial para plaza colón
        if (site.id == "plaza-colon") return distance < READY_RADIUS_IN_METERS_PLAZA

        return distance < READY_RADIUS_IN_METERS
    }

    companion object {
        const val TRACKING_STOP = "com.jackemate.appberdi.tracking.stop"
        const val TRACKING_UPDATES = "com.jackemate.appberdi.tracking.broadcast"
        const val EXTRA_UPDATE_POS = "pos"
        const val EXTRA_UPDATE_MODE = "mode"

        const val ACTION_FORCE = "com.jackemate.appberdi.tracking.action.UPDATE"
        const val ACTION_SELECT = "com.jackemate.appberdi.tracking.action.SELECT"
        const val ACTION_NAVIGATE = "com.jackemate.appberdi.tracking.action.NAVIGATE"

        const val EXTRA_SITE = "site"

        const val READY_RADIUS_IN_METERS = 50f
        const val READY_RADIUS_IN_METERS_PLAZA = 100f
    }
}