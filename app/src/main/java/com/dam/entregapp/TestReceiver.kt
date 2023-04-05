package com.dam.entregapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Stop the service here
        context.stopService(Intent(context, LocationService::class.java))
    }
}
