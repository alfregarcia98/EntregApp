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

//    fun distanceBetweenLocations(lat: Double, long: Double): String {
//
//        val currentLocation = Location("locationA")
//        currentLocation.latitude = lat
//        currentLocation.longitude = long
//        val destination = Location("locationB")
//        destination.latitude = 41.400524
//        destination.longitude = 2.163368
//
//        val distance = currentLocation.distanceTo(destination).toDouble()
//
//        if (distance < 100) {
//            available()
//        } else {
//            noAvailable()
//        }
//        //Log.d("Distancia", "Distance between two Geographic Locations: $distance KMS")
//        //return ("Distance between two Geographic Locations: $distance KMS")
//        return distance.toString()
//    }
//
//    fun available() {
//        //Añadimos la localizacion a tracking data
//        val tsLong = System.currentTimeMillis()
//        val timeStamp = Timestamp(tsLong)
//
//
//        //val tracking = TrackingData(null, 1, 1, timeStamp)
//        //repository.addTrackingData(tracking)
//        Log.d("Distancia", "AVAILABLE : $timeStamp")
//    }
//
//    fun noAvailable() {
//        //Descartamos la localizacion pero lo conteamos al total para las estadisticas
//        Log.d("Distancia", "NOT AVAILABLE")
//
//    }
//}
