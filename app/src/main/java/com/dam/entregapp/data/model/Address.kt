package com.dam.entregapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "address_table")
data class Address(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val user_id: Int,
    val name: String,
    val start_hour: String,
    val end_hour: String,
    val lon: Double,
    val lat: Double
) : Parcelable
