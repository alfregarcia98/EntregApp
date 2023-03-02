package com.dam.entregapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User
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

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(user)
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllUsers()
        }
    }

    //Address

    fun addAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAddress(address)
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAddress(address)
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

    suspend fun getTrackingWithAddrID(id: Int): List<TrackingData> {
        return repository.getTrackingWithAddrID(id)
    }
}