package com.dam.entregapp.ui

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.location.LocationService
import com.dam.entregapp.alarm.AlarmItem
import com.dam.entregapp.alarm.AndroidAlarmScheduler
import com.dam.entregapp.databinding.ActivityMainBinding
import com.dam.entregapp.ui.fragments.manageaddress.ManageAddress
import com.dam.entregapp.ui.viewmodels.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    val user = Firebase.auth.currentUser
    val email = ""
    private val db = Firebase.firestore
    val fragment = ManageAddress()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        title = "EntregApp"


        /*        //AlarmScheduler
                val scheduler = AndroidAlarmScheduler(this)
                var alarmItem: AlarmItem? = null*/


        //Para al hacer tap en la notificación abrir la aplicacion
        // Create an explicit intent for an Activity in your app

        //Al hacer click en la notificacion se abre la app con los parametros en el intent
        val bundle = intent.extras
        if (bundle != null) {
            //Si este parametro está quiere decir que el bundle tiene parametros de una noificacion
            if (bundle.getString("from") != null)
                alertCreator("Confirme si estará disponible para la entrega en la dirección X")
            for (key in bundle.keySet()) {
                Log.d(TAG, "$key is a key in the bundle")
            }
            var mensaje1 = bundle.getString("text")
            if (mensaje1 != null) {
                Log.d(TAG, "Bundle: ${bundle.getString("text")}")
                binding.ubicacion.text = mensaje1
                alertCreator(mensaje1)


            }
        }


        //Compruebo usuario
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            checkUser()
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
    }

    //Para la notificacion
    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageReceiver, IntentFilter("MyData"))

    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)

    }

    //Para la confirmacion de la notificacion in-app
    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val mensaje = intent.extras?.getString("message")
            if (mensaje != null) {
                alertCreator(mensaje)
            }
        }
    }

    fun alertCreator(mensaje: String) {
        val docRef = db.collection("users").document(prefs.getAuthID())
        val builder = AlertDialog.Builder(this)

        with(builder) {
            setTitle("EntregApp")
            setMessage(mensaje)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
            setPositiveButton("Si") { _, _ ->
                Toast.makeText(
                    applicationContext,
                    "Confirmado", Toast.LENGTH_SHORT
                ).show()
            }

            setPositiveButton("OK") { _, _ ->
                val confirmacion = hashMapOf(
                    "disponibilidad" to "Si",
                    "fecha" to FieldValue.serverTimestamp()
                )
                docRef.collection("Informacion").document("Disponibilidad")
                    .set(confirmacion)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            this@MainActivity,
                            "Confirmación guardada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@MainActivity,
                            "Error al guardar confirmación: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            setNegativeButton("No") { _, _ ->
                Toast.makeText(
                    applicationContext,
                    "Denegado", Toast.LENGTH_SHORT
                ).show()
            }

            show()
        }
    }


    fun getToken() {
        //Notificaciones
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val tokenGuardado = prefs.getDeviceID()
            val docRef = db.collection("users").document(prefs.getAuthID())
            docRef.collection("Informacion").document("Token")
                .set(
                    hashMapOf(
                        "token" to prefs.getDeviceID()
                    )
                )
            if (token != null) {
                if (tokenGuardado.isEmpty() || token != tokenGuardado) {
                    //registramos el token en el servidor
                    prefs.saveDeviceID(token)
                }
            }

            // Log and toast
            Log.d("Token", token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    suspend fun checkUser() {
        if (user != null) {
            user?.let {
                // Name, email address, and profile photo Url
                val email = it!!.email!!
                //ID unico del usuario en FBAuth
                val uid = it.uid
                val userIDs = mainViewModel.getUserID(email)
                if (userIDs.isNotEmpty()) {
                    var userID = userIDs[0]
                    Log.d("userID", "id: $userID")
                    prefs.saveEmail(email)
                    prefs.saveAuthID(uid)
                    prefs.saveCurrentUserID(userID)
                    setup(email)
                    mainViewModel.saveUserPrefs(userID)
                    getToken()
                } else {
                    Log.d("CheckUser", "userIDs was empty")
                }
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setup(userEmail: String) {
        runOnUiThread {
            binding.txtEmail.text = userEmail
        }

        binding.btLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            prefs.wipe()
            onBackPressed()
        }

        binding.btnMainMenu.setOnClickListener {
            val a = Intent(this, MenuActivity::class.java)
            startActivity(a)
        }

        binding.startService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        binding.stopService.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }

        //AlarmScheduler para notificacion
        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        binding.btnNotificacionOn.setOnClickListener {
            alarmItem = AlarmItem(8, "Notificacion")
            alarmItem?.let(scheduler::schedule)
        }

        binding.btnNotificacionOFF.setOnClickListener {
            alarmItem?.let(scheduler::cancel)
        }
    }
}