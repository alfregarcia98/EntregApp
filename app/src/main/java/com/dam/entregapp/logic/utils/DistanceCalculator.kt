package com.dam.entregapp.logic.utils

import android.location.Location
import android.util.Log

object DistanceCalculator {

    fun distanceBetweenLocations(): String {

        val currentLocation = Location("locationA")
        currentLocation.latitude = 17.372102
        currentLocation.longitude = 78.484196
        val destination = Location("locationB")
        destination.latitude = 17.375775
        destination.longitude = 78.469218

        val distance = currentLocation.distanceTo(destination).toDouble()

        Log.d("Distancia", "Distance between two Geographic Locations: $distance KMS")
        return ("Distance between two Geographic Locations: $distance KMS")

    }
}