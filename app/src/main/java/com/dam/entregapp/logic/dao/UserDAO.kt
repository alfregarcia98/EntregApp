package com.dam.entregapp.logic.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.User

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    @Transaction
    @Query("SELECT * FROM user_table WHERE id = :id")
    suspend fun getUserWithAddress(id: Int): List<UserWithAddress>

    //Address
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Query("SELECT * FROM address_table ORDER BY id DESC")
    fun getAllAddress(): LiveData<List<Address>>

    @Query("DELETE FROM address_table")
    suspend fun deleteAllAddress()
}
