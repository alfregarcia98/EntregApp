package com.dam.entregapp.alarm

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.dam.entregapp.LocationService


class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered: $message")

        if (intent.action.equals("STOP_TEST_SERVICE", ignoreCase = true)) {
            if (isMyServiceRunning(context, LocationService::class.java)) {
                Toast.makeText(context, "Service is running!! Stopping...", Toast.LENGTH_LONG)
                    .show()
                context.stopService(Intent(context, LocationService::class.java))
            } else {
                Toast.makeText(context, "Service not running", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}