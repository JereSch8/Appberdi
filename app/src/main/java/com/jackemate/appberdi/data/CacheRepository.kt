package com.jackemate.appberdi.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.jackemate.appberdi.utils.TAG
import java.io.File

class CacheRepository(private val context: Context) {

    /**
     * Devuelve el directorio para el sitio site dentro del directorio específico de la app.
     */
    fun getSiteStorageDir(site: String): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), site)
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        return file
    }

    fun clearSite(site: String) = getSiteStorageDir(site).deleteRecursively()
}