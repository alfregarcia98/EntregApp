package com.dam.entregapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dam.entregapp.LocationApp.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var cuenta = 0

        btpulsa.setOnClickListener {
            cuenta++
            contador.text = "Has pulsado: $cuenta veces"
            Toast.makeText(this, "Has pulsado: $cuenta veces", Toast.LENGTH_SHORT).show()
        }

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            txt_ubi.text = "${LocationService.lat}, ${LocationService.long}"
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()

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




    /**Prueba
    private lateinit var mService: LocationService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocationService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }
    */

    private fun setup(email: String, provider: String){
        val db = Firebase.firestore
        title = "Inicio"
        txt_email.text = email
        txt_provider.text = provider

        bt_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            prefs.wipe()
            onBackPressed()
        }

        btn_guardar.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider,
                    "phone" to txt_telefono.text.toString(),
                    "name" to txt_nombre.text.toString()))

            db.collection("users").document(email).collection("location").document("coordenadas").set(
                hashMapOf("time" to FieldValue.serverTimestamp(),
                    "lat" to LocationService.lat,
                    "long" to LocationService.long)
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
            /** Bind to LocalService
            Intent(this, LocationService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }*/
        }



        stopService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
                /**
                unbindService(connection)
                mBound = false*/

            }
        }
        btn_random.setOnClickListener {
            /**if (mBound) {
                val num: Int = mService.randomNumber
                Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()*/
                Toast.makeText(this, "Location: ${LocationService.lat}, ${LocationService.long}", Toast.LENGTH_SHORT).show()
            //}
        }

    }
}