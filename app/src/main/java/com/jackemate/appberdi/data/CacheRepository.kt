package com.jackemate.appberdi.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.jackemate.appberdi.R
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

    fun download(url: String, destination: String) {
        val uri = Uri.parse(url)
        val destinationUri = Uri.parse("$FILE_BASE_PATH$destination")
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle("Descargando contenido")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationUri(destinationUri)
//        request.setMimeType("application/pdf")
        downloadManager.enqueue(request)
        return
    }

    companion object {
        private const val FILE_BASE_PATH = "file://"
    }
}