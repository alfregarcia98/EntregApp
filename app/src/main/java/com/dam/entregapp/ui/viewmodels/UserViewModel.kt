package com.dam.entregapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.dao.TrackingDataResult
import com.dam.entregapp.logic.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    val getAllUsers: LiveData<List<User>>

    init {
        val userDao = UserDB.getDatabase(application).userDao()
        repository = UserRepository(userDao)

        getAllUsers = repository.getAllUsers
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

    suspend fun getUserWithAddress(id: Int): List<UserWithAddress> {
        return repository.getUserWithAddress(id)
    }

    //Address

    fun addAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAddress(address)
        }
    }

    fun deleteAllAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllAddress()
        }
    }
    //TrackingData

    suspend fun deleteAllTrackingData() {
        repository.deleteAllTrackingData()
    }

    suspend fun getTrackingData(id: Int): List<TrackingDataResult> {
        return repository.getTrackingData(id)
    }
}