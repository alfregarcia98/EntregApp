package com.dam.entregapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.logic.repository.UserRepository


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = UserDB.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }


    suspend fun getUserID(email: String): List<Int> {
        return repository.getUserID(email)
    }

    suspend fun getUserWithAddress(id: Int): List<UserWithAddress> {
        return repository.getUserWithAddress(id)
    }

    suspend fun saveUserPrefs(userID: Int) {
        val lista = getUserWithAddress(userID)
        if (lista.isNotEmpty()) {
            val username = lista[0].user.name
            val phone = lista[0].user.telephone.toString()
            prefs.saveName(username)
            prefs.savePhone(phone)
            Log.d("Prueba", "Lista: $lista")
            if (lista[0].addresses.isNotEmpty()) {
                //TODO un contador de numero de direcciones añadidas por el usuario en el fragment y aqui se comprueba eso.
                //if (lista[0].addresses.size == 2) {
                if (lista[0].addresses.size >= 2) {

                    Log.d("Prueba", "Lista: $lista")
                    val primaryName = lista[0].addresses[0].name
                    val secondaryName = lista[0].addresses[1].name
                    val primaryID = lista[0].addresses[0].id
                    val secondaryID = lista[0].addresses[1].id
                    Log.d("Prueba", "Primary: $primaryName, Secondary: $secondaryName")
                    val primaryLat = lista[0].addresses[0].lat.toFloat()
                    val primaryLon = lista[0].addresses[0].lon.toFloat()
                    val secondaryLat = lista[0].addresses[1].lat.toFloat()
                    val secondaryLon = lista[0].addresses[1].lon.toFloat()

                    prefs.savePrimaryAddressID(primaryID)
                    prefs.saveSecondaryAddressID(secondaryID)
                    prefs.savePrimaryAddressLat(primaryLat)
                    prefs.savePrimaryAddressLon(primaryLon)
                    prefs.saveSecondaryAddressLat(secondaryLat)
                    prefs.saveSecondaryAddressLon(secondaryLon)
                    prefs.savePrimaryAddressName(primaryName)
                    prefs.saveSecondaryAddressName(secondaryName)

                    Log.d(
                        "Prueba",
                        "PrimaryLat: $primaryLat, PrimaryLon: $primaryLon, SecondaryLat: $secondaryLat, SecondaryLon: $secondaryLon"
                    )

                    if (lista[0].addresses.size >= 3) {
                        Log.d("Prueba", "Lista: $lista")
                        val thirdName = lista[0].addresses[2].name
                        val thirdID = lista[0].addresses[2].id
                        Log.d("Prueba", "Primary: $primaryName, Secondary: $secondaryName")
                        val thirdLat = lista[0].addresses[2].lat.toFloat()
                        val thirdLon = lista[0].addresses[2].lon.toFloat()

                        prefs.saveThirdAddressID(thirdID)
                        prefs.saveThirdAddressLat(thirdLat)
                        prefs.saveThirdAddressLon(thirdLon)
                        prefs.saveThirdAddressName(thirdName)

                        Log.d(
                            "Prueba",
                            "ThirdLat: $thirdLat, ThirdLon: $thirdLon"
                        )
                    }

                    //TODO mejorar las condiciones de comprobación para distintas situaciones
                    if (lista[0].addresses.size == 4){
                        Log.d("Prueba", "Lista: $lista")
                        val fourthName = lista[0].addresses[3].name
                        val fourthID = lista[0].addresses[3].id
                        val fourthLat = lista[0].addresses[3].lat.toFloat()
                        val fourthLon = lista[0].addresses[3].lon.toFloat()


                        prefs.saveFourthAddressID(fourthID)
                        prefs.saveFourthAddressLat(fourthLat)
                        prefs.saveFourthAddressLon(fourthLon)
                        prefs.saveFourthAddressName(fourthName)

                        Log.d(
                            "Prueba",
                            "FourthLat: $fourthLat, FourthLon: $fourthLon"
                        )
                    }
                }
            }
        }
    }
}