package com.jackemate.appberdi.ui.cultural

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ktx.toObject
import com.jackemate.appberdi.data.CulturalRepository
import com.jackemate.appberdi.entities.CulturalActivity
import kotlinx.coroutines.tasks.await

class CulturalViewModel(app: Application) : AndroidViewModel(app) {
    private val culturalRepo = CulturalRepository()

    fun getCulturalActivities() = liveData {
        val docs = culturalRepo.getAll().get().await()
        val list = docs.documents.mapNotNull { it.toObject<CulturalActivity?>() }
        emit(list)
    }
}