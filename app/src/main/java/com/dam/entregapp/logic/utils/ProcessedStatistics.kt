package com.dam.entregapp.logic.utils

import kotlinx.serialization.Serializable

@Serializable
data class ProcessedStatistics(
    val startHour: Int,
    val endHour: Int,
    val resolutionMin: Int,
    var addressIds: List<Int> = emptyList(),
    var data: ArrayList<ArrayList<Double>> = ArrayList(listOf(ArrayList()))
)

