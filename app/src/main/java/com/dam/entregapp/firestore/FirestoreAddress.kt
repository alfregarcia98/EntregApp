package com.dam.entregapp.firestore

data class FirestoreAddress(
    val id: Int,
    val user_id: Int,
    val name: String,
    val start_hour: String,
    val end_hour: String,
    val lon: Double,
    val lat: Double
){
    constructor() : this(0, 0, "", "", "", 0.0, 0.0)
}
