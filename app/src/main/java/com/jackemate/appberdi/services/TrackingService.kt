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
import com.jackemate.appberdi.data.NotifyRepository
import com.jackemate.appberdi.data.PreferenceRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.Position
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.entities.TourMode
import com.jackemate.appberdi.ui.map.SiteMarker
import com.jackemate.appberdi.utils.Constants
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.hasPermission
import kotlin.math.roundToInt

class TrackingService : Service() {
    private lateinit var notifyRepo: NotifyRepository
    private lateinit var siteRepo: SiteRepository
    private lateinit var preferenceRepo: PreferenceRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentPos: LatLng? = null
    private var currentSites: List<SiteMarker>? = null
    private var currentMode: TourMode = TourMode.Thinking

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val loc = locationResult.locations.firstOrNull() ?: return
            currentPos = LatLng(loc.latitude, loc.longitude)
            doUpdate()
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 20000
        // Es poco tiempo pero para las pruebas se ve bien
        // Sinó queda desfazada la linea con el puntito azul
        fastestInterval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")
        notifyRepo = NotifyRepository(this)
        siteRepo = SiteRepository()
        preferenceRepo = PreferenceRepository(this)
        startForeground(NotifyRepository.NOTIFICATION_ID, notifyRepo.foreground())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(NotifyRepository.NOTIFICATION_ID, notifyRepo.foreground())
        Log.d(TAG, "onStartCommand")

        initSites()
        when (intent.action) {
            ACTION_FORCE -> doUpdate()
            ACTION_SELECT -> {
                val id = intent.getStringExtra(EXTRA_SITE)!!
                val site = currentSites!!.find { it.id == id }!!
                val distance = currentPos?.let { distanceTo(site.pos).toInt() }
                currentMode = TourMode.Selected(site, distance)
                broadcast()
            }
            ACTION_NAVIGATE -> {
                val best = computeBestSite()
                currentMode = if (best != null) {
                    val distance = distanceTo(best.pos).roundToInt()
                    TourMode.Navigating(best, distance)
                } else {
                    TourMode.Thinking
                }
                broadcast()
            }
        }

        return START_STICKY
    }

    override fun onLowMemory() {
        super.onLowMemory()
        stopLocationUpdates()
    }

    override fun onDestroy() {
        stopSelf()
        stopLocationUpdates()
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

    private fun doUpdate() {
        Log.d(TAG, "doUpdate: currentPos: $currentPos, status: $currentMode")

        if (currentSites == null) {
            Log.w(TAG, "no update! currentSites is null")
            return
        }

        when (currentMode) {
            is TourMode.Thinking,
            is TourMode.Navigating -> {
                val best = computeBestSite()
                Log.d(TAG, "best: $best")

                if (best != null) {
                    val distance = distanceTo(best.pos).roundToInt()
                    currentMode = TourMode.Navigating(best, distance)
                    broadcast()

                    if (distanceTo(best.pos) < Constants.GEOFENCE_RADIUS_IN_METERS) {
                        // todo: notificacion! broadcast
                    }
                }
            }
            is TourMode.Selected -> {
                broadcast()
            }
        }
    }

    private fun broadcast() {
        val broadcast = Intent()
        broadcast.action = TRACKING_UPDATES
        broadcast.putExtra(EXTRA_UPDATE_POS, currentPos)
        broadcast.putExtra(EXTRA_UPDATE_MODE, currentMode)
        broadcast.setPackage("com.jackemate.appberdi")
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

    companion object {
        const val TRACKING_UPDATES = "com.jackemate.appberdi.tracking.broadcast"
        const val EXTRA_UPDATE_POS = "pos"
        const val EXTRA_UPDATE_MODE = "mode"


        const val ACTION_FORCE = "com.jackemate.appberdi.tracking.action.UPDATE"
        const val ACTION_SELECT = "com.jackemate.appberdi.tracking.action.SELECT"
        const val ACTION_NAVIGATE = "com.jackemate.appberdi.tracking.action.NAVIGATE"

        const val EXTRA_SITE = "site"
    }
}