package com.jackemate.appberdi.io

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.util.Log
import com.jackemate.appberdi.io.BasicIO.makeDir
import com.jackemate.appberdi.io.BasicIO.urlImg
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

fun saveImage(context: Context, img: Bitmap, nameImg: String) {
    val file = makeDir(context, "$urlImg/$nameImg.jpeg")
    try {
        val fOut = FileOutputStream(file)
        img.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
        fOut.flush()
        fOut.close()
        checkFile(context, file)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "SaveImage: FilenotFoundException", e.cause)
    } catch (e: IOException) {
        Log.e(TAG, "SaveImage: IOException", e.cause)
    }
}

//Todo: Revisar si hace falta, en caso de que sea necesario hay que hacerlo correr en un hilo distinto al principal
fun getBitmapFromURL(strURL: String?): Bitmap? {
    return try {
        val url = URL(strURL)
        BitmapFactory.decodeStream(url.openConnection().getInputStream())
    } catch (e: IOException) {
        Log.e(TAG, "getBitmapFromURL: ${e.message}" )
        null
    }
}

private fun checkFile(context: Context, file: File) {
    MediaScannerConnection.scanFile(
        context, arrayOf(file.toString()), null
    ) { _, _ -> }
}