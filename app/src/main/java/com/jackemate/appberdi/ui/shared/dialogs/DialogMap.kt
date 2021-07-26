package com.jackemate.appberdi.ui.shared.dialogs

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jackemate.appberdi.databinding.DialogMapBinding
import com.jackemate.appberdi.entities.Attraction
import com.jackemate.appberdi.ui.shared.DialogBuilder


class DialogMap(context: Context, private val attraction: Attraction) : DialogBuilder(context) {
    override val binding = DialogMapBinding.inflate(inflater)

    init {
        val mMapView = binding.mapView
        MapsInitializer.initialize(context)

        mMapView.onCreate(onSaveInstanceState())
        mMapView.onResume()

        mMapView.getMapAsync { googleMap ->
            val pos = LatLng(attraction.pos!!.latitude, attraction.pos.longitude)
            googleMap.addMarker(MarkerOptions().position(pos).title(attraction.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17f), 2000, null)
        }
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}