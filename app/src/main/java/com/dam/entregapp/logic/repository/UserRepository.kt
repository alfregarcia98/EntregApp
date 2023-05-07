package com.dam.entregapp.logic.repository

import androidx.lifecycle.LiveData
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.dao.TrackingDataResult
import com.dam.entregapp.logic.dao.UserDAO

class UserRepository(private val userDAO: UserDAO) {
    val getAllUsers: LiveData<List<User>> = userDAO.getAllUsers()
    val getAllAddress: LiveData<List<Address>> = userDAO.getAllAddress()
    val getAllTrackingData: LiveData<List<TrackingData>> = userDAO.getAllTrackingData()

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

    suspend fun getUserID(email: String): Int {
        return userDAO.getUserID(email)[0]
    }

    suspend fun getUserWithAddress(id: Int): List<UserWithAddress> {
        return userDAO.getUserWithAddress(id)
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

    suspend fun getTrackingData(id: Int): List<TrackingDataResult> {
        return userDAO.getTrackingData(id)
    }

    suspend fun deleteAllTrackingData() {
        userDAO.deleteAllTrackingData()
    }
}