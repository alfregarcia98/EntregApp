package com.dam.entregapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.w3c.dom.Text

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
        var cuenta = 0

        btpulsa.setOnClickListener {
            cuenta++
            contador.text = "Has pulsado: $cuenta veces"
            Toast.makeText(this, "Has pulsado: $cuenta veces", Toast.LENGTH_SHORT).show()
        }

        //Setup
        val bundle = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        //LocationTutorial

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
    }




    private fun setup(email: String, provider: String){
        val db = Firebase.firestore
        title = "Inicio"
        txt_email.text = email
        txt_provider.text = provider
        bt_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        btn_guardar.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider,
                    "phone" to txt_telefono.text.toString(),
                    "name" to txt_nombre.text.toString())
            )
        }
        btn_recuperar.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                txt_telefono.setText(it.get("phone") as String?)
                txt_nombre.setText(it.get("name") as String?)
            }
        }
        btn_eliminar.setOnClickListener {
            db.collection("users").document(email).delete()
        }

        startService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        stopService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }
}