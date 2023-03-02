package com.dam.entregapp

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.data.database.UserDB
import com.dam.entregapp.data.model.TrackingData
import com.dam.entregapp.logic.repository.UserRepository
import com.dam.entregapp.logic.utils.DistanceCalculator
import com.google.android.gms.location.LocationServices
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

    private val MIN_PROXIMITY = 100.0
    var trackingCounter = 0

    //Probando
    // Binder given to clients
    private val binder = LocalBinder()

    // Random number generator
    private val mGenerator = Random()


    /** method for clients  */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
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
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
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

        val primaryLocation = Location("primaryLocation")
        primaryLocation.latitude = prefs.getPrimaryAddressLat().toDouble()
        primaryLocation.longitude = prefs.getPrimaryAddressLon().toDouble()
        val secondaryLocation = Location("secondaryLocation")
        secondaryLocation.latitude = prefs.getSecondaryAddressLat().toDouble()
        secondaryLocation.longitude = prefs.getSecondaryAddressLon().toDouble()
        if (trackingCounter == 0) {
            trackingCounter = prefs.getTrackingCount()
        }

        locationClient
            .getLocationUpdates(5000L)
            .catch { e -> e.printStackTrace() }
            .onEach { currentLocation ->
                if (DistanceCalculator.areLocationsWithinDistance(
                        primaryLocation,
                        currentLocation,
                        MIN_PROXIMITY
                    )
                ) {
                    // save this datapoint to the db with primary location id
                    val currentTime =
                        currentLocation.time.toString() //TODO dejarlo en long para luego operar con el
                    val tracking = TrackingData(0, userID, userPrimaryAddressID, currentTime)
                    repository.addTrackingData(tracking)

                } else if (DistanceCalculator.areLocationsWithinDistance(
                        secondaryLocation,
                        currentLocation,
                        MIN_PROXIMITY
                    )
                ) {
                    // save this datapoint to the db with secondary location id
                    val currentTime =
                        currentLocation.time.toString() //TODO dejarlo en long para luego operar con el
                    val tracking = TrackingData(0, userID, userSecondaryAddressID, currentTime)
                    repository.addTrackingData(tracking)
                } else {
                    // log out
                    Log.d("LOCATION_UPDATE", "No esta disponible")
                }

                trackingCounter++
                prefs.saveTrackingCount(trackingCounter)


                /*lat = currentLocation.latitude
                long = currentLocation.longitude
                val updatedNotification = notification.setContentText("Location: ($lat, $long)")
                Log.d("LOCATION_UPDATE", "Ubicacion: $lat, $long")

                notificationManager.notify(1, updatedNotification.build())
                var dist = DistanceCalculator.distanceBetweenLocations(lat, long)
                Log.d("LOCATION_UPDATE", "Disctancia: $dist")*/
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

