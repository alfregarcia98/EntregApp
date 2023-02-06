package com.dam.entregapp.logic.utils

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object Geocoder {


 /** Funcion que recibe un String con la direccion y devuelve las coordenadas de dicha direccion
  * haciendo uso de una api (positionstack)
  */
 suspend fun forwardGeocode(Address : String) : Pair<Double, Double> {

  val client = HttpClient()
//  val client = OkHttpClient()
  var response: HttpResponse


   response = client.get("http://api.positionstack.com/v1/forward") {
    url {
     parameters.append("access_key", "6dde42cd8f9c77849398a675529ffe26")
     parameters.append("query", Address)
    }
   }


   Log.d("Geocoding", response.toString())

   client.close()


  /**
  var httpBuilder: HttpUrl.Builder =
  "http://api.positionstack.com/v1/forward".toHttpUrlOrNull()!!.newBuilder()

  httpBuilder.addQueryParameter("access_key", "6dde42cd8f9c77849398a675529ffe26")
  .addQueryParameter("query", Address)

  val request: Request = Request.Builder()
  .url(httpBuilder.build())
  .build()

  var responseBody: String
  client.newCall(request).execute().use { response ->
  if (!response.isSuccessful) throw IOException("Unexpected code $response")


  responseBody = response.body!!.string()
  }


  return parseResult(responseBody)
  }
   **/
  return parseResult(response)
 }
 private fun parseResult(response: HttpResponse) : Pair<Double ,Double> {

  val jsonObject: JSONObject = JSONObject(response.toString())
  val data: JSONObject = jsonObject.getJSONObject("data")
  val results: JSONArray = data.getJSONArray("results")
  val result: JSONObject = results.getJSONObject(0)

  val lat = result.getDouble("latitude")
  val long = result.getDouble("longitude")

  return Pair(lat, long)
 }
}