package com.safiri.ridedelivery.util

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safiri.ridedelivery.R

/** Receives FCM pushes (new order, ride accepted, etc.) and shows a notification. */
class RideDeliveryMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "Ride & Delivery"
        val body = message.notification?.body ?: ""
        val notification = NotificationCompat.Builder(this, "ride_delivery_channel")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()
        getSystemService(NotificationManager::class.java).notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        // TODO: persist token to the user's Firestore doc for targeted pushes.
    }
}
