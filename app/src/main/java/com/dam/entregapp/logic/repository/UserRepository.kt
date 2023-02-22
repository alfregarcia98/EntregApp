package com.dam.entregapp.logic.repository

import androidx.lifecycle.LiveData
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.dao.UserDAO

class UserRepository(private val userDAO: UserDAO) {
    val getAllUsers: LiveData<List<User>> = userDAO.getAllUsers()

    //User
    suspend fun addUser(user: User) {
        userDAO.addUser(user)
    }

    suspend fun updateUser(user: User) {
        userDAO.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDAO.deleteUser(user)
    }

    suspend fun deleteAllUsers() {
        userDAO.deleteAllUsers()
    }

    //Address

    suspend fun addAddress(address: Address) {
        userDAO.addAddress(address)
    }

    suspend fun deleteAddress(address: Address) {
        userDAO.deleteAddress(address)
    }

    suspend fun deleteAllAddress() {
        userDAO.deleteAllAddress()
    }

    //TrackingData
    suspend fun addTrackingData(tracking: TrackingData) {
        userDAO.addTrackingData(tracking)
    }
}