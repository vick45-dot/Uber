package com.safiri.ridedelivery.navigation

/** Centralised route constants. */
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val FOOD = "food"
    const val RESTAURANT_MENU = "restaurant/{id}"
    const val RIDE = "ride"
    const val SEARCH = "search"
    const val AUTH = "auth"
    const val PROFILE = "profile"
    const val CART = "cart"
    const val ORDER_TRACKING = "orderTracking/{orderId}"
    const val RIDE_TRACKING = "rideTracking/{rideId}"
    const val PAYMENT = "payment"
    const val DRIVER_DASH = "driverDash"
    const val RIDER_DASH = "riderDash"
    const val RESTAURANT_DASH = "restaurantDash"
    const val ADMIN_DASH = "adminDash"

    fun restaurantMenu(id: String) = "restaurant/$id"
    fun orderTracking(id: String) = "orderTracking/$id"
    fun rideTracking(id: String) = "rideTracking/$id"
}
