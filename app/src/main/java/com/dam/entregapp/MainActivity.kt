package com.dam.entregapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

enum class ProviderType{
    BASIC
}

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
        val botoncont = findViewById<Button>(R.id.btpulsa)
        var cuenta = 0

        botoncont.setOnClickListener {
            cuenta++
            contador.text = "Has pulsado: $cuenta veces"
            Toast.makeText(this, "Has pulsado: $cuenta veces", Toast.LENGTH_SHORT).show()
        }

        //Setup
        val bundle = intent.extras
        val email : String? = bundle?.getString("email")
        val provider : String? = bundle?.getString("provider")
        setup(email ?: "",provider ?:"")
    }


    private fun setup(email: String, provider: String){
        title = "Inicio"
        val txt_email = findViewById<TextView>(R.id.txt_email)
        val txt_provider = findViewById<TextView>(R.id.txt_provider)
        txt_email.text = email
        txt_provider.text = provider
        val botonlogout = findViewById<Button>(R.id.bt_logout)
        botonlogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}