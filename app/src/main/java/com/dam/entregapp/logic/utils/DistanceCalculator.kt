package com.dam.entregapp.logic.utils

import android.location.Location
import android.util.Log
import com.dam.entregapp.LocationService

object DistanceCalculator {
    fun areLocationsWithinDistance(
        one: Location,
        other: Location,
        distance: Double
    ): Boolean {
        var enrango = distanceBetweenLocations(one, other) <= distance

        Log.d("DistanceCalculator", "Esta en rango: $enrango")
        return enrango
    }

    fun distanceBetweenLocations(one: Location, other: Location): Double {
        var distancia = one.distanceTo(other).toDouble()
        Log.d("DistanceCalculator", "Distancia: $distancia")
        Log.d("DistanceCalculator", "Ubi1: $one, Ubi2: $other")
        return distancia
    }
}