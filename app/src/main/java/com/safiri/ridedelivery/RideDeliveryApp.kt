package com.safiri.ridedelivery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.android.libraries.places.api.Places
import com.safiri.ridedelivery.data.repository.AppConfig

/** Application entry: initialises Places SDK (if a key is set) and the notification channel. */
class RideDeliveryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialise Places only if a real Maps key is present; harmless to skip in offline demo.
        val key = getString(R.string.maps_api_key)
        if (key.isNotBlank() && key != "YOUR_MAPS_API_KEY" && !Places.isInitialized()) {
            runCatching { Places.initialize(applicationContext, key) }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ride_delivery_channel",
                "Ride & Delivery",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
