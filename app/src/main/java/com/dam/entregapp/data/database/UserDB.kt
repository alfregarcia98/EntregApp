package com.dam.entregapp.data.database

import android.content.Context
import androidx.room.*
import com.dam.entregapp.data.converters.Converters
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.StatisticsData
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.data.model.User
import com.dam.entregapp.logic.dao.UserDAO

@Database(
    entities = [User::class, Address::class, TrackingData::class, StatisticsData::class],
    version = 13,
    exportSchema = false
)
@TypeConverters(Converters::class)
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