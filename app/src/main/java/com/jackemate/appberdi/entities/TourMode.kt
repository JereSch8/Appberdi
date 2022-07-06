package com.jackemate.appberdi.entities

import com.jackemate.appberdi.ui.map.SiteMarker
import java.io.Serializable

sealed class TourMode: Serializable {
    object Thinking: TourMode()
    data class Navigating(val best: SiteMarker, val distance: Int) : TourMode()
    data class Selected(val site: SiteMarker, val distance: Int?) : TourMode()
    data class Ready(val site: SiteMarker) : TourMode()
}