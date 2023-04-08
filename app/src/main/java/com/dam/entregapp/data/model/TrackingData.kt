package com.dam.entregapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "tracking_data_table")
data class TrackingData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val user_id: Int,
    val address_id: Int,
    val date: Date?
) : Parcelable