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
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogMapBinding
import com.jackemate.appberdi.entities.Attraction
import com.jackemate.appberdi.utils.LocalInfo


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

        if(LocalInfo(context).getAvatar() != -8)
            setAnimation(LocalInfo(context).getAvatar() )
        else
            setAnimation(R.raw.astronaut_dog)

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

    fun show(){
        dialog.setContentView(binding.root)
        dialog.show()
    }

    private fun setAnimation(rawRes: Int) = binding.animation.setAnimation(rawRes)

}