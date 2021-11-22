package com.jackemate.appberdi.data

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import androidx.lifecycle.liveData
import com.jackemate.appberdi.entities.Content
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.File


class CacheRepository(private val context: Context) {
    private val client = OkHttpClient()

    private fun getCacheDirFor(directory: String): File {
        val file = File(context.cacheDir, directory)
        if (!file.exists() && !file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        return file
    }

    private fun getCacheDirFor(content: Content.Cacheable): File = getCacheDirFor(content.tag)

    private fun getCacheFileFor(content: Content.Cacheable): File {
        val filename = URLUtil.guessFileName(content.href, null, null)
        return File(getCacheDirFor(content), filename)
    }

    private fun persist(content: Content.Cacheable): File {
        val out = getCacheFileFor(content)
        Log.d(TAG, "Persisting $content into ${out.absolutePath}")
        download(content.href, out)
        return out
    }

    fun get(content: Content.Cacheable) = liveData(Dispatchers.IO) {
        val diskCache = getCacheFileFor(content)
//        delay(5000)
        if (diskCache.exists()) {
            Log.i(TAG, "Returning ${content.tag} from disk cache")
            emit(diskCache)
        } else {
            val persisted = persist(content)
            emit(persisted)
        }
    }

    private fun download(link: String, out: File) {
        val request = Request.Builder().url(link).build()

        Log.i(TAG, "Downloading $link")
        val response = client.newCall(request).execute()

        response.body().byteStream().apply {
            out.outputStream().use { fileOut ->
                copyTo(fileOut)
            }
        }
    }

    /*
     * Tamaño ocupado por contenidos de tipo tag en megabytes (mil kilobytes).
     */
    fun sizeOf(tag: String) = sizeOfInBytes(getCacheDirFor(tag)) / 1000.0 / 1000.0

    /*
     * Tamaño de un directorio en bytes.
     *
     * No se preocupa por subdirectorios.
     *
     * Devuelve 0 si no hay archivos.
     */
    private fun sizeOfInBytes(file: File) = file
        .listFiles()
        .orEmpty()
        .fold(0L) { acc, f -> acc + f.length() }

    /*
     * Elimina el contenido del $tag
     */
    fun clear(tag: String) = getCacheDirFor(tag).deleteRecursively()

    companion object {
        const val TAG = "CacheRepository"
    }
}