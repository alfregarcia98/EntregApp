package com.dam.entregapp.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenViewModel : ViewModel() {
    val user = Firebase.auth.currentUser

    fun checkIfUserIsLoggedIn(
        onLoginNeeded: () -> Unit,
        onLoginNotNeeded: () -> Unit
    ) {
        if (user != null) {
            onLoginNotNeeded()
        } else {
            onLoginNeeded()
        }
    }

}