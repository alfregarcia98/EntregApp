package com.dam.entregapp.logic.utils

data class ProcessedStatistics(
    val startHour: Int,
    val endHour: Int,
    val resolutionMin: Int,
    var addressIds: List<Int> = emptyList(),
    var data: ArrayList<ArrayList<Double>> = ArrayList(listOf(ArrayList()))
)

