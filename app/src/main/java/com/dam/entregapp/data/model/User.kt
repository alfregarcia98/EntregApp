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
    val email: String,
    val password: String,
    val telephone: Int
) : Parcelable
