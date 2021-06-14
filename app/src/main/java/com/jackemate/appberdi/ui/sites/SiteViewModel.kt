package com.jackemate.appberdi.ui.sites

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.entities.ContentSite
import com.jackemate.appberdi.entities.Site
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class SiteViewModel : ViewModel() {
    private val siteRepo = SiteRepository()
    private val contentRepo = ContentRepository()

    private var mediaPlayer = MediaPlayer()

    fun getSite(idSite: String) = liveData {
        val doc = siteRepo.getSite(idSite).get().await()
        val site = doc.toObject<Site?>()

        site?.let {
            Log.i(TAG, "site tour size: ${site.tour.size}")
            val contents = site.tour.mapNotNull {
                contentRepo.fromDoc(it.get().await())
            }
            Log.i(TAG, "site parsed contents size: ${contents.size}")
            emit(ContentSite(site, contents))
        }
    }

    fun preStartAudio(url: String, prepared: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { prepared() }
    }

    fun playPauseAudio(): Boolean {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        return mediaPlayer.isPlaying
    }

    fun pauseAudio() {
        mediaPlayer.pause()
    }

    fun onDisconnect() {
        mediaPlayer.release()
    }

    fun forwardAudio() = changeCurrentAudio(5000)
    fun rewindAudio() = changeCurrentAudio(-5000)

    private fun changeCurrentAudio(change: Int): String {
        if ((change > 0 && (mediaPlayer.isPlaying && mediaPlayer.duration != mediaPlayer.currentPosition))
            || (change < 0 && (mediaPlayer.isPlaying && mediaPlayer.currentPosition > kotlin.math.abs(
                change
            )))
        ) {
            val newCurrentPosition = mediaPlayer.currentPosition + change
            mediaPlayer.seekTo(newCurrentPosition)

            return convertFormat(newCurrentPosition.toLong())
        }
        return convertFormat(mediaPlayer.duration.toLong())
    }


    fun getDurationMedia() = convertFormat(mediaPlayer.duration.toLong())


    fun setMediaPlayerSeekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

    fun updateProgressAudio(seekBar: SeekBar, tvCurrentAudio: TextView) {
        seekBar.max = mediaPlayer.duration
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                tvCurrentAudio.text = convertFormat(mediaPlayer.currentPosition.toLong())
                handler.postDelayed(this, 500)
            }
        }
        if (!mediaPlayer.isPlaying)
            handler.removeCallbacks(runnable)
        else
            handler.postDelayed(runnable, 0)
    }

    private fun convertFormat(duration: Long) = String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(duration),
        TimeUnit.MILLISECONDS.toSeconds(duration) % 60
    )
}