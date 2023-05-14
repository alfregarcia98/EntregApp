package com.dam.entregapp.logic.utils

import android.location.Location

object DistanceCalculator {
    fun areLocationsWithinDistance(
        one: Location,
        other: Location,
        distance: Double
    ): Boolean {
        return distanceBetweenLocations(one, other) <= distance
    }

    fun distanceBetweenLocations(one: Location, other: Location): Double {
        return one.distanceTo(other).toDouble()
    }
}