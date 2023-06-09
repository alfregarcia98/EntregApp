package com.dam.entregapp.logic.utils

import android.util.Log
import com.dam.entregapp.model.GeocoderService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Geocoder {

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://api.positionstack.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getGeocoder(
        address: String,
        onResult: (latLong: Pair<Double, Double>) -> Unit
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {

            var lon: Double = 0.0
            var lat: Double = 0.0

            val call = getRetrofit().create(GeocoderService::class.java)
                .listGeocoderResult("6dde42cd8f9c77849398a675529ffe26", "$address", "1")
            val geocoder = call.execute().body()
            Log.d("getGeocoder", "Print: $geocoder")

            lon = geocoder!!.data[0].longitude
            lat = geocoder!!.data[0].latitude

            onResult(Pair(lon, lat))

            Log.d("Geocoder", "Ubicacion: $lon,$lat")
        }
    }
}

