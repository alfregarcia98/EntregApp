package com.dam.entregapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val age: Int,
    val email:String,
    val password: String,
    val telephone: Int ) {
}