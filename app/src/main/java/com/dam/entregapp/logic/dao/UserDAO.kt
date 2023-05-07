package com.dam.entregapp.logic.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User

@Dao
interface UserDAO {

    //User
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT id FROM user_table WHERE email = :email")
    suspend fun getUserID(email: String): List<Int>

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    //Relation 1-M
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

    //TrackingData
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackingData(track: TrackingData)

    @Query("SELECT * FROM tracking_data_table ORDER BY id DESC")
    fun getAllTrackingData(): LiveData<List<TrackingData>>

    @Query("SELECT address_id,  strftime('%H',date/1000, 'unixepoch','localtime') as hour, count(id) as data_count from tracking_data_table where (user_id= :id)group by hour,address_id order by hour;")
    suspend fun getTrackingData(id: Int): List<TrackingDataResult>

    @Query("DELETE FROM tracking_data_table")
    suspend fun deleteAllTrackingData()
}
