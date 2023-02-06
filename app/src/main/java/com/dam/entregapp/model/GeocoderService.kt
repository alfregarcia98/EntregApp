package com.dam.entregapp.model


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocoderService {
    @GET("forward")
    fun listGeocoderResult(@Query("access_key") api_key: String,@Query("query") query: String, @Query("limit") limit: String): Call<GeocoderData>
}