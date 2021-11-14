package com.jackemate.appberdi.ui.mediateca

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.ContentRepository
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediatecaViewModel(val context: Application) : AndroidViewModel(context) {
    private val repoContent = ContentRepository(context)

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


    fun getContents(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        getImages(nameSite)
        getAudios(nameSite)
        getGifs(nameSite)
        getVideos(nameSite)
        getText(nameSite)
    }


    private fun getImages(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentImageWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _images.value = value!!.mapNotNull { it.toObject() }
        }
    }

    private fun getAudios(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentAudioWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _audios.value = value!!.mapNotNull { it.toObject() }
        }
    }

    private fun getGifs(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentGifWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _gifs.value = value!!.mapNotNull { it.toObject() }
        }
    }

    private fun getVideos(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentVideoWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _videos.value = value!!.mapNotNull { it.toObject() }
        }
    }

    private fun getText(nameSite : String) = viewModelScope.launch(Dispatchers.IO) {
        repoContent.getContentTextWhere(nameSite).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            _texts.value = value!!.mapNotNull { it.toObject() }
        }
    }

}

