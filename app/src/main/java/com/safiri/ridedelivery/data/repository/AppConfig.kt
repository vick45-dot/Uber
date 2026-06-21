package com.safiri.ridedelivery.data.repository

/**
 * Single switch controlling the data backend.
 *
 *  false -> fully offline, in-memory mock data (no Firebase, no billing).
 *  true  -> live Firebase (Auth + Firestore). Requires google-services.json
 *           and a Firestore database to be set up.
 *
 * Flip to true once you've enabled the free (default) Native Firestore database.
 */
object AppConfig {
    const val USE_FIREBASE = false
}
