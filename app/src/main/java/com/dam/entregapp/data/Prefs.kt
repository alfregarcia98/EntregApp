package com.dam.entregapp.data

import android.content.Context

class Prefs(val context: Context) {

    val SHARED_NAME = "Database"
    val SHARED_USERNAME = "username"
    val SHARED_PASSWORD = "password"
    val SHARED_USERID = "userID"
    val SHARED_PRIMARYLAT = "primarylat"
    val SHARED_PRIMARYLON = "primarylon"
    val SHARED_SECONDARYLAT = "secondarylat"
    val SHARED_SECONDARYLON = "secondarylon"
    val SHARED_PRIMARYID = "primaryAddressID"
    val SHARED_SECONDARYID = "secondaryAddressID"
    val SHARED_PRIMARYNAME = "primaryAddressName"
    val SHARED_SECONDARYNAME = "secondaryAddressName"
    val SHARED_TRACKINGCOUNT = "trackingtotalcount"

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

    //User
    fun saveCurrentUserID(userID: Int) {
        storage.edit().putInt(SHARED_USERID, userID).apply()
    }

    fun getCurrentUserID(): Int {
        return storage.getInt(SHARED_USERID, 0)!!
    }

    //Address
    fun savePrimaryAddressLat(primaryAddressLat: Float) {
        storage.edit().putFloat(SHARED_PRIMARYLAT, primaryAddressLat).apply()
    }

    fun getPrimaryAddressLat(): Float {
        return storage.getFloat(SHARED_PRIMARYLAT, 0F)!!
    }

    fun savePrimaryAddressLon(primaryAddressLon: Float) {
        storage.edit().putFloat(SHARED_PRIMARYLON, primaryAddressLon).apply()
    }

    fun getPrimaryAddressLon(): Float {
        return storage.getFloat(SHARED_PRIMARYLON, 0F)!!
    }

    fun saveSecondaryAddressLat(secondaryAddressLat: Float) {
        storage.edit().putFloat(SHARED_SECONDARYLAT, secondaryAddressLat).apply()
    }

    fun getSecondaryAddressLat(): Float {
        return storage.getFloat(SHARED_SECONDARYLAT, 0F)!!
    }

    fun saveSecondaryAddressLon(secondaryAddressLon: Float) {
        storage.edit().putFloat(SHARED_SECONDARYLON, secondaryAddressLon).apply()
    }

    fun getSecondaryAddressLon(): Float {
        return storage.getFloat(SHARED_SECONDARYLON, 0F)!!
    }

    fun savePrimaryAddressID(primaryAddID: Int) {
        storage.edit().putInt(SHARED_PRIMARYID, primaryAddID).apply()
    }

    fun getPrimaryAddressID(): Int {
        return storage.getInt(SHARED_PRIMARYID, 0)!!
    }

    fun saveSecondaryAddressID(secondaryAddID: Int) {
        storage.edit().putInt(SHARED_SECONDARYID, secondaryAddID).apply()
    }

    fun getSecondaryAddressID(): Int {
        return storage.getInt(SHARED_SECONDARYID, 0)!!
    }

    fun savePrimaryAddressName(primaryAddName: String) {
        storage.edit().putString(SHARED_PRIMARYNAME, primaryAddName).apply()
    }

    fun getPrimaryAddressName(): String {
        return storage.getString(SHARED_PRIMARYNAME, "")!!
    }

    fun saveSecondaryAddressName(secondaryAddName: String) {
        storage.edit().putString(SHARED_SECONDARYNAME, secondaryAddName).apply()
    }

    fun getSecondaryAddressName(): String {
        return storage.getString(SHARED_SECONDARYNAME, "")!!
    }

    fun saveTrackingCount(count: Int) {
        storage.edit().putInt(SHARED_TRACKINGCOUNT, count).apply()
    }

    fun getTrackingCount(): Int {
        return storage.getInt(SHARED_TRACKINGCOUNT, 0)!!
    }

    fun resetTrackingCounter() {
        storage.edit().remove(SHARED_TRACKINGCOUNT).apply()
    }

    fun wipe() {
        storage.edit().clear().apply()
    }

}