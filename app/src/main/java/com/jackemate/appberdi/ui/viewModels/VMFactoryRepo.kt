package com.jackemate.appberdi.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jackemate.appberdi.domain.repository.Repo

class VMFactoryRepo(
    private val repo: Repo
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Repo::class.java).newInstance( repo) }

}