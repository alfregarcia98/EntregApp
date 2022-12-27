package com.dam.entregapp

import android.app.Application
import androidx.room.Room

class UserApp : Application() {

    val room = Room.databaseBuilder(this, UserDB::class.java, "user").build()
}