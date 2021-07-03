package com.jackemate.appberdi.io

import android.content.Context
import android.os.Environment
import java.io.File
import java.math.RoundingMode
import java.util.*

object BasicIO {
    const val urlImg: String = "img"
    const val urlVideo: String = "video"
    const val urlAudio: String = "audio"

    fun deleteDirIMG(context: Context) = deleteDir(makeDir(context, urlImg))
    fun deleteDirVIDEO(context: Context) = deleteDir(makeDir(context, urlVideo))
    fun deleteDirAUDIO(context: Context) = deleteDir(makeDir(context, urlAudio))

    fun sizeStorage(context: Context) =
        (sizeDirIMG(context) + sizeDirVIDEO(context) + sizeDirAUDIO(context))
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

    fun sizeDirIMG(context: Context) = getFileSize(makeDir(context, urlImg))
    fun sizeDirVIDEO(context: Context) = getFileSize(makeDir(context, urlVideo))
    fun sizeDirAUDIO(context: Context) = getFileSize(makeDir(context, urlAudio))

    internal fun makeDir(context: Context, dir: String) = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), dir
    )

    private fun deleteDir(dir: File) {
        if (dir.isDirectory) {
            val child = dir.list()

            for (i in child!!.indices) {
                File(dir, child[i]).delete()
            }
        }
    }

    private fun getFileSize(file: File?): Double {
        var result = 0.0
        if (file == null || !file.exists()) return result
        if (!file.isDirectory) return file.length().toDouble()

        val dirs: MutableList<File> = LinkedList()
        dirs.add(file)

        while (dirs.isNotEmpty()) {
            val dir = dirs.removeAt(0)
            if (!dir.exists()) continue
            val listFiles = dir.listFiles()
            if (listFiles.isNullOrEmpty()) continue
            for (child in listFiles) {
                result += child.length()
                if (child.isDirectory) dirs.add(child)
            }
        }
        return (result / 1024 / 1024)
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }
}