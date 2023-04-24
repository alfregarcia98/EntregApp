package com.dam.entregapp.logic.utils

data class ProcessedStatistics(
    val startHour: Int,
    val endHour: Int,
    val resolutionMin: Int,
    var address_id: ArrayList<Int> = ArrayList(),
    var data: ArrayList<ArrayList<Double>> = ArrayList()
)

