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

//Todo lo anterior, pendiente de analizar y limpiar
/** Funcion que recibe un String con la direccion y devuelve las coordenadas de dicha direccion
 * haciendo uso de una api (positionstack)
 */
/**
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
}*/


