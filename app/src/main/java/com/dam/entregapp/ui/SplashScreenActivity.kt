package com.dam.entregapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dam.entregapp.R

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var spalshScreenViewModel: SplashScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        spalshScreenViewModel = ViewModelProvider(this)[SplashScreenViewModel::class.java]
        spalshScreenViewModel.checkIfUserIsLoggedIn(
            onLoginNeeded = {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            },
            onLoginNotNeeded = {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        )
    }


}