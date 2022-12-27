package com.dam.entregapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    //Room
    //val app = applicationContext as UserApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*ROOM
        lifecycleScope.launch {
            val users = app.room.userDao().getAll()
            Log.d("", "onCreate: ${users.size} users")
        }
*/
        val contador = findViewById<TextView>(R.id.contador)
        val boton = findViewById<Button>(R.id.btpulsa)
        var cuenta = 0

        boton.setOnClickListener {
            cuenta++
            contador.text = "Has pulsado: $cuenta veces"
            Toast.makeText(this, "Has pulsado: $cuenta veces", Toast.LENGTH_LONG).show()
        }
    }
}