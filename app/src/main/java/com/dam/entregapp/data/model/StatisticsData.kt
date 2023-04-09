package com.dam.entregapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "statistics_data_table")
data class StatisticsData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val hora: Int,
    val address_id: Int,
    val count: Int
) : Parcelable