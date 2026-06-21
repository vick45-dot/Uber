package com.safiri.ridedelivery.util

import com.safiri.ridedelivery.data.model.GeoPoint
import com.safiri.ridedelivery.data.model.VehicleType
import kotlin.math.*

/**
 * Fare estimation tuned for the Kenyan market (KES).
 * Uses base fare + per-km + per-minute, with a per-vehicle multiplier.
 */
object FareCalculator {

    private data class Tariff(val base: Double, val perKm: Double, val perMin: Double, val min: Double)

    private val tariffs = mapOf(
        VehicleType.BODA_BODA  to Tariff(base = 50.0,  perKm = 25.0, perMin = 2.0, min = 80.0),
        VehicleType.MOTORCYCLE to Tariff(base = 50.0,  perKm = 25.0, perMin = 2.0, min = 80.0),
        VehicleType.TAXI       to Tariff(base = 150.0, perKm = 60.0, perMin = 4.0, min = 250.0),
        VehicleType.CAR        to Tariff(base = 120.0, perKm = 45.0, perMin = 3.0, min = 200.0),
    )

    /** Haversine distance in kilometres. */
    fun distanceKm(a: GeoPoint, b: GeoPoint): Double {
        val r = 6371.0
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLng = Math.toRadians(b.lng - a.lng)
        val h = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(a.lat)) * cos(Math.toRadians(b.lat)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(h), sqrt(1 - h))
    }

    /** Returns estimated fare in KES, rounded to nearest 10. */
    fun estimate(type: VehicleType, pickup: GeoPoint, destination: GeoPoint): Double {
        val t = tariffs[type] ?: tariffs.getValue(VehicleType.CAR)
        val km = distanceKm(pickup, destination)
        val estMinutes = km / 30.0 * 60.0   // assume ~30 km/h average city speed
        val raw = t.base + km * t.perKm + estMinutes * t.perMin
        val fare = max(raw, t.min)
        return (round(fare / 10.0) * 10.0)
    }
}
