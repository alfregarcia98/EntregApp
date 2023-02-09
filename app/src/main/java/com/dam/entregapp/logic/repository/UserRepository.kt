package com.dam.entregapp.logic.repository

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.dao.UserDAO

class UserRepository(private val userDAO: UserDAO) {
    val getAllUsers: LiveData<List<User>> = userDAO.getAllUsers()

    suspend fun addUser(user: User){
        userDAO.addUser(user)
    }

    suspend fun updateUser(user: User){
        userDAO.updateUser(user)
    }

    suspend fun deleteUser(user: User){
        userDAO.deleteUser(user)
    }

    suspend fun deleteAllUsers(){
        userDAO.deleteAllUsers()
    }

    //Address

    suspend fun addAddress(address: Address){
        userDAO.addAddress(address)
    }

    suspend fun deleteAddress(address: Address){
        userDAO.deleteAddress(address)
    }

    suspend fun deleteAllAddress(){
        userDAO.deleteAllAddress()
    }
}