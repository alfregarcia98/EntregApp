package com.dam.entregapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.logic.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun addTracking(tracking: TrackingData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTrackingData(tracking)
        }
    }

    suspend fun deleteAllTrackingData() {
        repository.deleteAllTrackingData()
    }

    suspend fun saveUserPrefs(userID: Int) {
        val lista = getUserWithAddress(userID)
        if (lista.isNotEmpty()) {
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

                    //Hecho en el Sync fragment
                    //saveUserDataToFirestore(lista)

                    Log.d(
                        "Prueba",
                        "PrimaryLat: $primaryLat, PrimaryLon: $primaryLon, SecondaryLat: $secondaryLat, SecondaryLon: $secondaryLon"
                    )

                    //TODO mejorar las condiciones de comprobación para distintas situaciones
                    if (lista[0].addresses.size == 4){
                        Log.d("Prueba", "Lista: $lista")
                        val thirdName = lista[0].addresses[2].name
                        val fourthName = lista[0].addresses[3].name
                        val thirdID = lista[0].addresses[2].id
                        val fourthID = lista[0].addresses[3].id
                        Log.d("Prueba", "Primary: $primaryName, Secondary: $secondaryName")
                        val thirdLat = lista[0].addresses[2].lat.toFloat()
                        val thirdLon = lista[0].addresses[2].lon.toFloat()
                        val fourthLat = lista[0].addresses[3].lat.toFloat()
                        val fourthLon = lista[0].addresses[3].lon.toFloat()

                        prefs.saveThirdAddressID(thirdID)
                        prefs.saveFourthAddressID(fourthID)
                        prefs.saveThirdAddressLat(thirdLat)
                        prefs.saveThirdAddressLon(thirdLon)
                        prefs.saveFourthAddressLat(fourthLat)
                        prefs.saveFourthAddressLon(fourthLon)
                        prefs.saveThirdAddressName(thirdName)
                        prefs.saveFourthAddressName(fourthName)

                        Log.d(
                            "Prueba",
                            "ThirdLat: $thirdLat, ThirdLon: $thirdLon, FourthLat: $fourthLat, FourthLon: $fourthLon"
                        )
                    }
                }
            }
        }
    }
}