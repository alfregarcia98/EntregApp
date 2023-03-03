package com.dam.entregapp

import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dam.entregapp.LocationApp.Companion.prefs
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val db = Firebase.firestore

    override fun onMessageReceived(message: RemoteMessage) {

        Looper.prepare()
        Handler().post() {
            Toast.makeText(baseContext, message.notification?.title, Toast.LENGTH_LONG).show()
        }

        Looper.loop()

    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {

        db.collection("users").document(prefs.getEmail()).collection("Notification")
            .document("Token")
            .set(
                hashMapOf(
                    "token" to token
                )
            )

    }
}