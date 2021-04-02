package com.jackemate.appberdi.ui.attractions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.AttractionRepository
import com.jackemate.appberdi.domain.entities.Attraction
import kotlinx.coroutines.tasks.await

class AttractionViewModel(app: Application) : AndroidViewModel(app) {
    private val attractionRepo = AttractionRepository()

    fun getAttractions() = liveData {
        val docs = attractionRepo.getAttractions().get().await()
        val list = docs.documents.mapNotNull { it.toObject<Attraction?>() }
        emit(list)
    }
}