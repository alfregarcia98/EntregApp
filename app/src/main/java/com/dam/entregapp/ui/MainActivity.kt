package com.dam.entregapp.ui

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
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
import com.dam.entregapp.ChartActivity
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.LocationService
import com.dam.entregapp.alarm.AlarmItem
import com.dam.entregapp.alarm.AndroidAlarmScheduler
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.databinding.ActivityMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        title = "Inicio"


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

/*
        //AlarmScheduler
        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null
        alarmItem = AlarmItem(10,"Alarma de detencion")
        alarmItem?.let(scheduler::schedule)*/




        //Para al hacer tap en la notificación abrir la aplicacion
        // Create an explicit intent for an Activity in your app



        //Al hacer click en la notificacion se habre la app con los parametros en el intent
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

        //Notificaciones
        getToken()

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
                docRef.collection("Informacion").document("Datos").collection("Entregas")
                    .document("Disponibilidad")
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
                val userID = mainViewModel.getUserID(email)
                prefs.saveEmail(email)
                prefs.saveAuthID(uid)
                setup(email, uid, userID)
                saveUserPrefs(userID)
                //binding.txtEmail.text = email
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }



    suspend fun saveUserPrefs(userID: Int) {
        val lista = mainViewModel.getUserWithAddress(userID)
        if (lista.isNotEmpty()) {
            Log.d("Prueba", "Lista: $lista")
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
                    prefs.savePrimaryAddressName(primaryName)
                    prefs.saveSecondaryAddressName(secondaryName)

                    //Hecho en el Sync fragment
                    //saveUserDataToFirestore(lista)

                    Log.d(
                        "Prueba",
                        "PrimaryLat: $primaryLat, PrimaryLon: $primaryLon, SecondaryLat: $secondaryLat, SecondaryLon: $secondaryLon"
                    )
                }
            }
        }

    }

    private fun saveUserDataToFirestore(lista: List<UserWithAddress>) {
        val docRef = db.collection("users").document(prefs.getAuthID())

        docRef.collection("Preferencias").document("Lista").set(lista[0])
    }

    private fun setup(userEmail: String,uid: String ,userID: Int) {
        val docRef = db.collection("users").document(uid)

        /**user?.let {
        // Name, email address, and profile photo Url
        val id = it.tenantId
        val email = it.email
        binding.txtEmail.text = email
        binding.txtProvider.text = id
        }*/
        runOnUiThread {
            binding.txtEmail.text = userEmail
            binding.txtProvider.text = userID.toString()
        }
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

            docRef.collection("Informacion").document("Datos")
                .set(
                    hashMapOf(
                        "name" to binding.txtNombre.text.toString(),
                        "token" to prefs.getDeviceID()
                    )
                )

        }
        binding.btnRecuperar.setOnClickListener {
            docRef.collection("Informacion").document("Datos").get().addOnSuccessListener {
                binding.txtNombre.setText(it.get("name") as String?)
            }
        }
        binding.btnEliminar.setOnClickListener {
            docRef.collection("Informacion").document("Datos").delete()
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

        /**if (mBound) {
        val num: Int = mService.randomNumber
        Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()*/

        binding.btnChart.setOnClickListener {
            val a = Intent(this, ChartActivity::class.java)
            startActivity(a)
        }
        //AlarmScheduler para notificacion
        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        binding.btnNotificacionOn.setOnClickListener{
            alarmItem = AlarmItem(8,"Notificacion")
            alarmItem?.let(scheduler::schedule)
        }

        binding.btnNotificacionOFF.setOnClickListener{
            alarmItem?.let(scheduler::cancel)
        }
    }
}