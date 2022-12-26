package com.dam.entregapp


import androidx.room.Dao
import androidx.room.Query

@Dao
class UserDAO {
    @Query("SELECT * FROM User")
    suspend fun getAll() {
    }

    @Query("SELECT * FROM User WHERE id =:id")
    suspend fun getById(id: Int) {
    }
}