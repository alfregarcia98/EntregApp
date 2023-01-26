package com.dam.entregapp.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.LocationService
import com.dam.entregapp.R
import com.dam.entregapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var cuenta = 0

        binding.btpulsa.setOnClickListener {
            cuenta++
            binding.contador.text = "Has pulsado: $cuenta veces"
            Toast.makeText(this, "Has pulsado: $cuenta veces", Toast.LENGTH_SHORT).show()
        }

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            binding.txtUbi.text = "${LocationService.lat}, ${LocationService.long}"
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
        binding.txtEmail.text = email
        binding.txtProvider.text = provider

        binding.btLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            prefs.wipe()
            onBackPressed()
        }

        binding.btnAddr.setOnClickListener {

        }

        binding.btnGuardar.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf("provider" to provider,
                    "phone" to binding.txtTelefono.text.toString(),
                    "name" to binding.txtNombre.text.toString()))

            db.collection("users").document(email).collection("location").document("coordenadas").set(
                hashMapOf("time" to FieldValue.serverTimestamp(),
                    "lat" to LocationService.lat,
                    "long" to LocationService.long
                )
            )
        }
        binding.btnRecuperar.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                binding.txtTelefono.setText(it.get("phone") as String?)
                binding.txtNombre.setText(it.get("name") as String?)
            }
        }
        binding.btnEliminar.setOnClickListener {
            db.collection("users").document(email).delete()
        }

        binding.startService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
            /** Bind to LocalService
            Intent(this, LocationService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }*/
        }



        binding.stopService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
                /**
                unbindService(connection)
                mBound = false*/

            }
        }
        binding.btnRandom.setOnClickListener {
            /**if (mBound) {
                val num: Int = mService.randomNumber
                Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()*/
                Toast.makeText(this, "Location: ${LocationService.lat}, ${LocationService.long}", Toast.LENGTH_SHORT).show()
            //}
        }

    }
}