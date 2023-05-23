package com.dam.entregapp.alarm

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dam.entregapp.location.LocationService
import com.dam.entregapp.R
import com.dam.entregapp.ui.MainActivity


class AlarmReceiver : BroadcastReceiver() {

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

        } else if (intent.action.equals("Notificacion", ignoreCase = true)) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, "alarm")
                .setContentTitle("EntregApp")
                .setContentText("Activa el servicio para mejorar tus estadísticas")
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            notificationManager.notify(2, notification.build())
            Log.d("Alarma", "Deberia saltar")
        }

        //No funciona porque hay un timeout de 5 segundos en el framework de android que provoca que de error
        /*else if (intent.action.equals("START_TEST_SERVICE", ignoreCase = true)) {
            if (isMyServiceRunning(context, LocationService::class.java)) {
                Toast.makeText(context, "Service is already running!!", Toast.LENGTH_LONG)
                    .show()
            } else {
                context.startForegroundService(Intent(context, LocationService::class.java))
                Toast.makeText(context, "Service not running. Starting service...", Toast.LENGTH_LONG).show()
            }
        }*/
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