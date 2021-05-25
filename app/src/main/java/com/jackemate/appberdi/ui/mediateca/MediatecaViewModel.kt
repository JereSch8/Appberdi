package com.jackemate.appberdi.ui.mediateca

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.domain.entities.Content
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediatecaViewModel : ViewModel() {
    private val repoContent = ContentRepository()

    val nameSites: List<String> = repoContent.getSites()
    val tags: List<String> = repoContent.getTags()

    private val _audios : MutableLiveData<List<Content.Audio>> = MutableLiveData()
    val audios: LiveData<List<Content.Audio>> = _audios
    private val _images : MutableLiveData<List<Content.Image>> = MutableLiveData()
    val images: LiveData<List<Content.Image>> = _images
    private val _gifs : MutableLiveData<List<Content.Gif>> = MutableLiveData()
    val gifs: LiveData<List<Content.Gif>> = _gifs
    private val _videos : MutableLiveData<List<Content.Video>> = MutableLiveData()
    val videos: LiveData<List<Content.Video>> = _videos
    private val _texts : MutableLiveData<List<Content.Text>> = MutableLiveData()
    val texts: LiveData<List<Content.Text>> = _texts

    fun getImages(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentImageWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _images.value = value!!.mapNotNull { it.toObject() }
        }
    }

    fun getAudios(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentAudioWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _audios.value = value!!.mapNotNull { it.toObject() }
        }
    }

    fun getGifs(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentGifWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _gifs.value = value!!.mapNotNull { it.toObject() }
        }
    }

    fun getVideos(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentVideoWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _videos.value = value!!.mapNotNull { it.toObject() }
        }
    }

    fun getText(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentTextWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _texts.value = value!!.mapNotNull { it.toObject() }
        }
    }

}

