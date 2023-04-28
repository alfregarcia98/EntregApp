package com.dam.entregapp.firestore

data class FirestoreUser(
    var email: String,
    var username: String,
    var phone: String
) {
    constructor() : this("", "", "")
}
