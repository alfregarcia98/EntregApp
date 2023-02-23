package com.dam.entregapp.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.LocationService
import com.dam.entregapp.databinding.ActivityMainBinding
import com.dam.entregapp.ui.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    val user = Firebase.auth.currentUser
    val email = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(4000)
                        runOnUiThread {
                            binding.txtUbi.text = "${LocationService.lat}, ${LocationService.long}"
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()

        //Compruebo usuario
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            checkUser()
        }

        //Setup en caso de querer los extras desde login y register
        //val bundle = intent.extras
        //val email: String? = bundle?.getString("email")
        //setup(email ?: "")

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

    suspend fun checkUser() {
        //val user = Firebase.auth.currentUser
        if (user != null) {
            user?.let {
                // Name, email address, and profile photo Url
                val email = it!!.email!!
                val userID = mainViewModel.getUserID(email)
                setup(email, userID)
                prueba(userID)
                //binding.txtEmail.text = email
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    suspend fun prueba(userID: Int) {
        val lista = mainViewModel.getUserWithAddress(userID)
        if (lista.isNotEmpty()) {
            if (lista[0].addresses.isNotEmpty()) {
                if (lista[0].addresses.size == 2) {

                    Log.d("Prueba", "Lista: $lista")
                    val primaryName = lista[0].addresses[0].name
                    val secondaryName = lista[0].addresses[1].name
                    val primaryID = lista[0].addresses[0].id
                    val secondaryID = lista[0].addresses[1].id
                    Log.d("Prueba", "Primary: $primaryName, Secondary: $secondaryName")
                    val primaryLat = lista[0].addresses[0].lat.toFloat()
                    val primaryLon = lista[0].addresses[0].lon.toFloat()
                    val secondaryLat = lista[0].addresses[1].lat.toFloat()
                    val secondaryLon = lista[0].addresses[1].lon.toFloat()

                    prefs.savePrimaryAddressID(primaryID)
                    prefs.saveSecondaryAddressID(secondaryID)
                    prefs.savePrimaryAddressLat(primaryLat)
                    prefs.savePrimaryAddressLon(primaryLon)
                    prefs.saveSecondaryAddressLat(secondaryLat)
                    prefs.saveSecondaryAddressLon(secondaryLon)

                    Log.d(
                        "Prueba",
                        "PrimaryLat: $primaryLat, PrimaryLon: $primaryLon, SecondaryLat: $secondaryLat, SecondaryLon: $secondaryLon"
                    )
                }
            }
        }

    }

    private fun setup(userEmail: String, userID: Int) {
        val db = Firebase.firestore
        title = "Inicio"
        /**user?.let {
        // Name, email address, and profile photo Url
        val id = it.tenantId
        val email = it.email
        binding.txtEmail.text = email
        binding.txtProvider.text = id
        }*/
        binding.txtEmail.text = userEmail
        binding.txtProvider.text = userID.toString()

        prefs.saveCurrentUserID(userID)

        binding.btLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            prefs.wipe()
            onBackPressed()
        }

        binding.btnMainMenu.setOnClickListener {
            val a = Intent(this, MainActivity2::class.java)
            startActivity(a)
        }

        binding.btnGuardar.setOnClickListener {
            db.collection("users").document(userEmail).set(
                hashMapOf(
                    "phone" to binding.txtTelefono.text.toString(),
                    "name" to binding.txtNombre.text.toString()
                )
            )

            db.collection("users").document(userEmail).collection("location")
                .document("coordenadas")
                .set(
                    hashMapOf(
                        "time" to FieldValue.serverTimestamp(),
                        "lat" to LocationService.lat,
                        "long" to LocationService.long
                    )
                )
        }
        binding.btnRecuperar.setOnClickListener {
            db.collection("users").document(userEmail).get().addOnSuccessListener {
                binding.txtTelefono.setText(it.get("phone") as String?)
                binding.txtNombre.setText(it.get("name") as String?)
            }
        }
        binding.btnEliminar.setOnClickListener {
            db.collection("users").document(userEmail).delete()
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
        binding.btnBorrar.setOnClickListener {
            /**if (mBound) {
            val num: Int = mService.randomNumber
            Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()*/
            CoroutineScope(Dispatchers.IO).launch {
                mainViewModel.deleteAllTrackingData()
            }
            //}
        }
    }
}