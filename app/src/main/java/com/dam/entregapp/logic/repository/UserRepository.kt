package com.dam.entregapp.logic.repository

import androidx.lifecycle.LiveData
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
        userDAO.deleteAll()
    }

}