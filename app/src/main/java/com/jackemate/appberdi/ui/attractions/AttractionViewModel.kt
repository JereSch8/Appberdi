package com.jackemate.appberdi.ui.attractions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.AttractionRepository
import com.jackemate.appberdi.entities.Attraction
import kotlinx.coroutines.tasks.await

class AttractionViewModel(app: Application) : AndroidViewModel(app) {
    private val attractionRepo = AttractionRepository()

    fun getAttractions() = liveData {
        val docs = attractionRepo.getAttractions().get().await()
        val list = docs.documents.mapNotNull { it.toObject<Attraction?>() }
        emit(list)
    }

    fun getOneAttraction(id: String) = liveData {
        val doc = attractionRepo.get(id).get().await()
        val list = doc.toObject<Attraction>() ?: throw Exception("Can't convert $id to Attraction")
        emit(list)
    }
}