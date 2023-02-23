package com.dam.entregapp

import android.content.Context

class Prefs(val context: Context) {

    val SHARED_NAME = "Database"
    val SHARED_USERNAME = "username"
    val SHARED_PASSWORD = "password"

    //Create a new field storing the actual user
    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveName(name: String) {
        storage.edit().putString(SHARED_USERNAME, name).apply()
    }

    fun getName(): String {
        return storage.getString(SHARED_USERNAME, "")!!
    }

    fun savePassword(password: String) {
        storage.edit().putString(SHARED_PASSWORD, password).apply()
    }

    fun getPassword(): String {
        return storage.getString(SHARED_PASSWORD, "")!!
    }

    fun saveCurrentUserID(userID: Int) {
        storage.edit().putInt(SHARED_USERNAME, userID).apply()
    }

    fun getCurrentUserID(): Int {
        return storage.getInt(SHARED_USERNAME, 0)!!
    }

    fun savePrimaryAddressID(primaryAddID: Int) {
        storage.edit().putInt(SHARED_USERNAME, primaryAddID).apply()
    }

    fun getPrimaryAddressID(): Int {
        return storage.getInt(SHARED_USERNAME, 0)!!
    }

    fun saveSecondaryAddressID(secondaryAddID: Int) {
        storage.edit().putInt(SHARED_USERNAME, secondaryAddID).apply()
    }

    fun getSecondaryAddressID(): Int {
        return storage.getInt(SHARED_USERNAME, 0)!!
    }

    fun wipe() {
        storage.edit().clear().apply()
    }

}