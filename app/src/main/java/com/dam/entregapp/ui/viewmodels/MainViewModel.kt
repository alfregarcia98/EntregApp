package com.dam.entregapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.logic.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = UserDB.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }


    suspend fun getUserID(email: String): Int {
        return repository.getUserID(email)
    }

    fun addTracking(tracking: TrackingData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTrackingData(tracking)
        }
    }
}