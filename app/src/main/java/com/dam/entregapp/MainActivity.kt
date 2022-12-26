package com.dam.entregapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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