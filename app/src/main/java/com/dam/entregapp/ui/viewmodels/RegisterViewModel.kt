package com.dam.entregapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    private val getAllUsers: LiveData<List<User>>

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
}