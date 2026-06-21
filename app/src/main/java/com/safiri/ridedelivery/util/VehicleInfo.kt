package com.safiri.ridedelivery.util

import com.safiri.ridedelivery.data.model.VehicleType

/** Display metadata for each vehicle type — used to make the ride selector visual. */
data class VehicleMeta(
    val type: VehicleType,
    val label: String,
    val emoji: String,
    val capacity: String,
    val tagline: String
)

object VehicleInfo {
    val all = listOf(
        VehicleMeta(VehicleType.BODA_BODA, "Boda Boda", "🛵", "1 seat", "Fastest through traffic"),
        VehicleMeta(VehicleType.MOTORCYCLE, "Motorcycle", "🏍️", "1 seat", "Quick and affordable"),
        VehicleMeta(VehicleType.CAR, "Car", "🚗", "4 seats", "Comfortable everyday ride"),
        VehicleMeta(VehicleType.TAXI, "Taxi", "🚕", "4 seats", "Premium door-to-door"),
    )

    fun of(type: VehicleType): VehicleMeta = all.first { it.type == type }
    fun emoji(type: VehicleType): String = of(type).emoji
}
