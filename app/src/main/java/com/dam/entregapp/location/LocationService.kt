package com.dam.entregapp.location

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dam.entregapp.R
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.logic.repository.UserRepository
import com.dam.entregapp.logic.utils.DistanceCalculator
import com.dam.entregapp.ui.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private val MIN_PROXIMITY = 250.0
    private val UPDATE_INTERVAL = 300000L // 5min

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("GPS no habilitado, activelo y reinicie el servicio")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val repository: UserRepository
        val userDao = UserDB.getDatabase(application).userDao()
        repository = UserRepository(userDao)

        //Get all the info relative to the user id and his addresses
        val userID = prefs.getCurrentUserID()
        val userPrimaryAddressID = prefs.getPrimaryAddressID()
        val userSecondaryAddressID = prefs.getSecondaryAddressID()
        val userThirdAddressID = prefs.getThirdAddressID()
        val userFourthAddressID = prefs.getFourthAddressID()

        val primaryLocation = Location("primaryLocation")
        primaryLocation.latitude = prefs.getPrimaryAddressLat().toDouble()
        primaryLocation.longitude = prefs.getPrimaryAddressLon().toDouble()
        val secondaryLocation = Location("secondaryLocation")
        secondaryLocation.latitude = prefs.getSecondaryAddressLat().toDouble()
        secondaryLocation.longitude = prefs.getSecondaryAddressLon().toDouble()

        val thirdLocation = Location("thirdLocation")
        thirdLocation.latitude = prefs.getThirdAddressLat().toDouble()
        thirdLocation.longitude = prefs.getThirdAddressLon().toDouble()
        val fourthLocation = Location("fourthLocation")
        fourthLocation.latitude = prefs.getFourthAddressLat().toDouble()
        fourthLocation.longitude = prefs.getFourthAddressLon().toDouble()

        val db = Firebase.firestore
        val docRef = db.collection("users").document(prefs.getAuthID())

        locationClient
            .getLocationUpdates(UPDATE_INTERVAL)
            .catch { e -> e.printStackTrace() }
            .onEach { currentLocation ->

                val confirmacion = hashMapOf(
                    "Last update" to FieldValue.serverTimestamp()
                )
                docRef.collection("Servicio").document("En ejecución")
                    .set(confirmacion)

                if (DistanceCalculator.areLocationsWithinDistance(
                        primaryLocation,
                        currentLocation,
                        MIN_PROXIMITY
                    )
                ) {
                    // save this datapoint to the db with primary location id
                    val currentTime = Date(currentLocation.time)
                    val tracking = TrackingData(0, userID, userPrimaryAddressID, currentTime)
                    repository.addTrackingData(tracking)

                } else if (DistanceCalculator.areLocationsWithinDistance(
                        secondaryLocation,
                        currentLocation,
                        MIN_PROXIMITY
                    )
                ) {
                    // save this datapoint to the db with secondary location id
                    val currentTime = Date(currentLocation.time)
                    val tracking = TrackingData(0, userID, userSecondaryAddressID, currentTime)
                    repository.addTrackingData(tracking)

                } else if (DistanceCalculator.areLocationsWithinDistance(
                        thirdLocation,
                        currentLocation,
                        MIN_PROXIMITY
                    )
                ) {
                    // save this datapoint to the db with third location id
                    val currentTime = Date(currentLocation.time)
                    val tracking = TrackingData(0, userID, userThirdAddressID, currentTime)
                    repository.addTrackingData(tracking)

                } else if (DistanceCalculator.areLocationsWithinDistance(
                        fourthLocation,
                        currentLocation,
                        MIN_PROXIMITY
                    )
                ) {
                    // save this datapoint to the db with fourth location id
                    val currentTime = Date(currentLocation.time)
                    val tracking = TrackingData(0, userID, userFourthAddressID, currentTime)
                    repository.addTrackingData(tracking)
                } else {
                    // save this datapoint to the db with location id=null
                    val currentTime = Date(currentLocation.time)
                    val tracking = TrackingData(0, userID, null, currentTime)
                    repository.addTrackingData(tracking)
                    Log.d("LOCATION_UPDATE", "No esta disponible")
                }

                lat = currentLocation.latitude
                long = currentLocation.longitude
                val updatedNotification = notification.setContentText("Location: ($lat, $long)")
                Log.d("LOCATION_UPDATE", "Ubicacion: $lat., $long")

                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var long = 0.0
        var lat = 0.0
    }
}

