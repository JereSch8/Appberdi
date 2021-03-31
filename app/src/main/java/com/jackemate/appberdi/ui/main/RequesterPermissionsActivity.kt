package com.jackemate.appberdi.ui.main

import android.Manifest
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.jackemate.appberdi.R
import com.jackemate.appberdi.utils.*

open class RequesterPermissionsActivity : AppCompatActivity() {
    private val listOfPermissionsCallbacks: MutableList<() -> Unit> = mutableListOf()

    private val fromPermissionToRationaleStringId: Map<String, Int> = mapOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE to R.string.permission_storage_required,
        Manifest.permission.ACCESS_COARSE_LOCATION to R.string.permission_location__required,
        Manifest.permission.ACCESS_FINE_LOCATION to R.string.permission_location__required,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to R.string.permission_location__required
    )

    fun withPermission(permission: String, callback: () -> Unit) {
        withPermissions(arrayOf(permission), callback)
    }

    fun withPermissions(permissions: Array<String>, callback: () -> Unit) {
        if (permissions.none { needPermission(it) }) {
            // Los permisos ya fueron otorgados, así que ejecutamos el callback inmediatamente.
            callback()
        } else {
            val index = listOfPermissionsCallbacks.size
            listOfPermissionsCallbacks.add(callback)
            requestPermissionOrShowRationale(permissions, index)
        }
    }

    private fun requestPermissionOrShowRationale(permissions: Array<String>, callbackIndex: Int) {
        val firstPermission = permissions[0]
        if (shouldShowRequestPermissionRationaleCompat(firstPermission)) {
            popUpPermissionRationale(permissions) {
                requestPermissionsCompat(permissions, callbackIndex)
            }
        } else {
            requestPermissionsCompat(permissions, callbackIndex)
        }
    }

    private fun popUpPermissionRationale(permissions: Array<String>, onOk: () -> Unit) {
        val firstPermission = permissions[0]
        val stringId: Int = fromPermissionToRationaleStringId[firstPermission]
            ?: R.string.permission_default_required
        val root: View = findViewById<View>(android.R.id.content).rootView

        root.showSnackbar(
            stringId,
            Snackbar.LENGTH_INDEFINITE,
            R.string.ok
        ) {
            onOk()
        }
    }

    override fun onRequestPermissionsResult(
        callbackIndex: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(callbackIndex, permissions, grantResults)
        Log.v(TAG, "onRequestPermissionsResult: callbackIndex = $callbackIndex")
        val callback = listOfPermissionsCallbacks.getOrNull(callbackIndex)

        if (callback == null) {
            Log.e(TAG, "onRequestPermissionsResult: callback indefinido.")
            return
        }

        if (permissions.none { needPermission(it) }) {
            // Si los permisos ya estan ortorgados
            callback()
        } else {
            // En este caso podríamos hacer varias cosas. Pero simplemente vamos a repetir el mensaje.
            popUpPermissionRationale(permissions) {
                // Si esta función se spamea, simplemente no se llama. Creo que se puede llamar máximo 2 veces.
                requestPermissionsCompat(permissions, callbackIndex)
            }
        }
    }
}