package com.jackemate.appberdi.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.view.Window
import android.view.WindowManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jackemate.appberdi.databinding.DialogMapBinding
import com.jackemate.appberdi.entities.Attraction


class DialogMap(){

    private lateinit var binding : DialogMapBinding
    private lateinit var dialog : Dialog

    constructor(context: Context, activity: Activity, attraction : Attraction) : this(){
        val inflater : LayoutInflater = LayoutInflater.from(context)
        binding = DialogMapBinding.inflate(inflater)
        dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val mMapView = binding.mapView
        MapsInitializer.initialize(activity)

        mMapView.onCreate(dialog.onSaveInstanceState())
        mMapView.onResume()

        mMapView.getMapAsync { googleMap ->
            val pos = LatLng(attraction.pos!!.latitude, attraction.pos.longitude)
            googleMap.addMarker(MarkerOptions().position(pos).title(attraction.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17f), 2000, null)
        }
    }

    fun make(){
        dialog.setContentView(binding.root)
        dialog.show()
    }

//    fun setAnimation(rawRes: Int) = binding.animation.setAnimation(rawRes)

}