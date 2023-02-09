package com.dam.entregapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
@Entity(tableName = "tracking_data_table")
data class TrackingData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val user_id: String,
    val address_id: String,
    val TimeStamp: Timestamp
) : Parcelable