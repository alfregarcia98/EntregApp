package com.dam.entregapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.dao.UserDAO

@Database(
    entities = [User::class, Address::class, TrackingData::class],
    version = 8,
    exportSchema = true
)
abstract class UserDB : RoomDatabase() {

    abstract fun userDao(): UserDAO

    companion object {

        @Volatile
        private var INSTANCE: UserDB? = null

        fun getDatabase(context: Context): UserDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDB::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}