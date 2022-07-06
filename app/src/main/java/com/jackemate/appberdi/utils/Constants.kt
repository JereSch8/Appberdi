package com.jackemate.appberdi.utils

import java.util.concurrent.TimeUnit

object Constants {
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(24)

}