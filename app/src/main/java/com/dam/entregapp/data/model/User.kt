package com.dam.entregapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val name: String,
    val email:String,
    val password: String,
    val telephone: Int,
    val addr1: String,
    val addr2: String
    ):Parcelable

/**
@Entity
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val email:String,
    val timeStamp: Timestamp,
    val long: Int,
    val lat: Int )**/