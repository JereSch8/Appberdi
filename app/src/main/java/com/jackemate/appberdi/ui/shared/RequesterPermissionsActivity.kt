package com.jackemate.appberdi.ui.shared

import android.Manifest
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.jackemate.appberdi.R
import com.jackemate.appberdi.utils.*

open class RequesterPermissionsActivity : AppCompatActivity() {
    protected lateinit var root: View

    private val listOfPermissionsCallbacks: MutableList<() -> Unit> = mutableListOf()
    private val fromPermissionToRationaleStringId: Map<String, Int> = mapOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE to R.string.permission_storage_required,
        Manifest.permission.ACCESS_COARSE_LOCATION to R.string.permission_location_required,
        Manifest.permission.ACCESS_FINE_LOCATION to R.string.permission_location_required,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to R.string.permission_background_required
    )

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
//        val root: View = findViewById<View>(android.R.id.content).rootView

        root.showSnackbar(
            stringId,
            Snackbar.LENGTH_INDEFINITE,
            R.string.change
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

        val pending = permissions.filter { needPermission(it) }
        if (pending.isEmpty()) {
            // Si los permisos ya estan ortorgados
            callback()
        } else {
            // En este caso podríamos hacer varias cosas. Pero simplemente vamos a repetir el mensaje.
            popUpPermissionRationale(pending.toTypedArray()) {
                // Si esta función se spamea, simplemente no se llama. Creo que se puede llamar máximo 2 veces.
                requestPermissionsCompat(pending.toTypedArray(), callbackIndex)
            }
        }
    }
}