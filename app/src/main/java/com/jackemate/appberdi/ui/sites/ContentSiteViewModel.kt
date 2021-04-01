package com.jackemate.appberdi.ui.sites

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.SiteRepository
import com.jackemate.appberdi.domain.entities.ContentAudio
import com.jackemate.appberdi.domain.entities.ContentImage
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ContentSiteViewModel : ViewModel() {
    private val repo = SiteRepository()

    private val _images : MutableLiveData<List<ContentImage>> = MutableLiveData()
    val images: LiveData<List<ContentImage>> = _images
    private val _audios : MutableLiveData<List<ContentAudio>> = MutableLiveData()
    val audios: LiveData<List<ContentAudio>> = _audios

    private var mediaPlayer =  MediaPlayer()

    fun printImage(context: Context, url: String, image: ImageView){
        Glide.with(context)
            .load(url)
            .error(R.drawable.no_image)
            .placeholder(R.drawable.loading)
            .centerCrop()
            .into(image)

        }


    fun preStartAudio(url : String, buttonPlay : ImageButton){
        buttonPlay.isEnabled = false
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
        }
    }

    fun startAudio(buttonPlay : ImageButton){
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            buttonPlay.setImageResource(R.drawable.ic_pause)
        }
        else {
            mediaPlayer.start()
            buttonPlay.setImageResource(R.drawable.ic_play)
        }

    }

    fun pauseAudio(){
        mediaPlayer.pause()
    }

    fun forwardAudio() = changeCurrentAudio(5000)
    fun rewindAudio() = changeCurrentAudio(-5000)

    private fun changeCurrentAudio(change : Int) : String{
        if((change>0 && (mediaPlayer.isPlaying && mediaPlayer.duration != mediaPlayer.currentPosition))
            || (change <0 && (mediaPlayer.isPlaying &&  mediaPlayer.currentPosition > kotlin.math.abs(change)))){
            val newCurrentPosition = mediaPlayer.currentPosition + change
            mediaPlayer.seekTo(newCurrentPosition)

            return convertFormat(newCurrentPosition.toLong())
        }
        return convertFormat(mediaPlayer.duration.toLong())
    }


    fun getDurationMedia() =  convertFormat(mediaPlayer.duration.toLong())


    fun setMediaPlayerSeekTo(progress : Int) { mediaPlayer.seekTo(progress) }

    fun updateProgressAudio(seekBar: SeekBar, tvCurrentAudio : TextView){
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
            handler.postDelayed(runnable,0)
    }

    fun getContentImageWhere(idSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repo.getContentImageWhere(idSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _images.value = value!!.mapNotNull { it.toObject() }
        }
    }

    fun getContentAudioWhere(idSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repo.getContentAudioWhere(idSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _audios.value = value!!.mapNotNull { it.toObject() }
        }
    }


    private fun convertFormat(duration : Long) = String.format("%02d:%02d",
                                                        TimeUnit.MILLISECONDS.toMinutes(duration),
                                                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                                             TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))



}