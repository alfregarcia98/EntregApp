package com.dam.entregapp.data

import android.content.Context

class Prefs(val context: Context) {

    val SHARED_NAME = "Database"
    val SHARED_USERNAME = "username"
    val SHARED_EMAIL = "email"
    val SHARED_PHONE = "phone"
    val SHARED_USERID = "userID"
    val SHARED_DEVICEID = "deviceID"
    val SHARED_AUTHID = "AuthID"
    val SHARED_PRIMARYLAT = "primarylat"
    val SHARED_PRIMARYLON = "primarylon"
    val SHARED_SECONDARYLAT = "secondarylat"
    val SHARED_SECONDARYLON = "secondarylon"
    val SHARED_THIRDLAT = "thirdlat"
    val SHARED_THIRDLON = "thirdlon"
    val SHARED_FOURTHLAT = "fourthlat"
    val SHARED_FOURTHLON = "fourthlon"
    val SHARED_PRIMARYID = "primaryAddressID"
    val SHARED_SECONDARYID = "secondaryAddressID"
    val SHARED_THIRDID = "thirdAddressID"
    val SHARED_FOURTHID = "fourthAddressID"
    val SHARED_PRIMARYNAME = "primaryAddressName"
    val SHARED_SECONDARYNAME = "secondaryAddressName"
    val SHARED_THIRDNAME = "thirdAddressName"
    val SHARED_FOURTHNAME = "fourthAddressName"
    val SHARED_INTERVAL = "updateInterval"

    //Create a new field storing the actual user
    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveName(name: String) {
        storage.edit().putString(SHARED_USERNAME, name).apply()
    }

    fun getName(): String {
        return storage.getString(SHARED_USERNAME, "")!!
    }

    fun saveDeviceID(token: String) {
        storage.edit().putString(SHARED_DEVICEID, token).apply()
    }

    fun getDeviceID(): String {
        return storage.getString(SHARED_DEVICEID, "")!!
    }

    fun saveAuthID(token: String) {
        storage.edit().putString(SHARED_AUTHID, token).apply()
    }

    fun getAuthID(): String {
        return storage.getString(SHARED_AUTHID, "")!!
    }

    fun saveEmail(email: String) {
        storage.edit().putString(SHARED_EMAIL, email).apply()
    }

    fun getEmail(): String {
        return storage.getString(SHARED_EMAIL, "")!!
    }

    fun savePhone(phone: String) {
        storage.edit().putString(SHARED_PHONE, phone).apply()
    }

    fun getPhone(): String {
        return storage.getString(SHARED_PHONE, "")!!
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

    fun saveThirdAddressLat(thirdAddressLat: Float) {
        storage.edit().putFloat(SHARED_THIRDLAT, thirdAddressLat).apply()
    }

    fun getThirdAddressLat(): Float {
        return storage.getFloat(SHARED_THIRDLAT, 0F)!!
    }

    fun saveThirdAddressLon(thirdAddressLon: Float) {
        storage.edit().putFloat(SHARED_THIRDLON, thirdAddressLon).apply()
    }

    fun getThirdAddressLon(): Float {
        return storage.getFloat(SHARED_THIRDLON, 0F)!!
    }

    fun saveFourthAddressLat(fourthAddressLat: Float) {
        storage.edit().putFloat(SHARED_FOURTHLAT, fourthAddressLat).apply()
    }

    fun getFourthAddressLat(): Float {
        return storage.getFloat(SHARED_FOURTHLAT, 0F)!!
    }

    fun saveFourthAddressLon(fourthAddressLon: Float) {
        storage.edit().putFloat(SHARED_FOURTHLON, fourthAddressLon).apply()
    }

    fun getFourthAddressLon(): Float {
        return storage.getFloat(SHARED_FOURTHLON, 0F)!!
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

    fun saveThirdAddressID(thirdAddID: Int) {
        storage.edit().putInt(SHARED_THIRDID, thirdAddID).apply()
    }

    fun getThirdAddressID(): Int {
        return storage.getInt(SHARED_THIRDID, 0)!!
    }

    fun saveFourthAddressID(fourthAddID: Int) {
        storage.edit().putInt(SHARED_FOURTHID, fourthAddID).apply()
    }

    fun getFourthAddressID(): Int {
        return storage.getInt(SHARED_FOURTHID, 0)!!
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

    fun saveThirdAddressName(thirdAddName: String) {
        storage.edit().putString(SHARED_THIRDNAME, thirdAddName).apply()
    }

    fun getThirdAddressName(): String {
        return storage.getString(SHARED_THIRDNAME, "")!!
    }

    fun saveFourthAddressName(fourthAddName: String) {
        storage.edit().putString(SHARED_FOURTHNAME, fourthAddName).apply()
    }

    fun getFourthAddressName(): String {
        return storage.getString(SHARED_FOURTHNAME, "")!!
    }

    fun wipe() {
        storage.edit().clear().apply()
    }

}