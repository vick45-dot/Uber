package com.safiri.ridedelivery.data.model

/**
 * Core domain models. All map cleanly to Firestore documents.
 * Every class has a no-arg default so Firestore's toObject() can deserialize.
 */

enum class UserRole { CUSTOMER, DRIVER, RIDER, RESTAURANT, ADMIN }
enum class VehicleType { CAR, TAXI, MOTORCYCLE, BODA_BODA }
enum class OrderStatus { PENDING, ACCEPTED, PREPARING, PICKED_UP, ON_THE_WAY, DELIVERED, CANCELLED }
enum class RideStatus { REQUESTED, ACCEPTED, ARRIVING, IN_PROGRESS, COMPLETED, CANCELLED }
enum class ApprovalStatus { PENDING, APPROVED, REJECTED }
enum class PaymentMethod { MPESA, CARD, CASH }

data class GeoPoint(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = ""
)

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val photoUrl: String = "",
    val savedAddresses: List<GeoPoint> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Restaurant(
    val id: String = "",
    val ownerUid: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val emoji: String = "🍽️",          // visual badge (offline-friendly)
    val category: String = "",
    val priceLevel: Int = 2,            // 1=cheap .. 4=premium
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val etaMinutes: Int = 30,
    val deliveryFee: Double = 150.0,
    val location: GeoPoint = GeoPoint(),
    val isOpen: Boolean = true
)

data class MenuItem(
    val id: String = "",
    val restaurantId: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val emoji: String = "🍲",            // visual badge
    val foodCategory: String = "Mains",  // Mains, Sides, Drinks, Dessert...
    val price: Double = 0.0,
    val popular: Boolean = false,
    val available: Boolean = true
)

data class Shop(
    val id: String = "",
    val ownerUid: String = "",
    val name: String = "",
    val category: String = "",
    val emoji: String = "🛒",
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val etaMinutes: Int = 40,
    val location: GeoPoint = GeoPoint()
)

data class Driver(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val vehicleType: VehicleType = VehicleType.CAR,
    val vehicleModel: String = "",
    val plateNumber: String = "",
    val vehiclePhotoUrl: String = "",
    val rating: Double = 5.0,
    val ratingCount: Int = 0,
    val approval: ApprovalStatus = ApprovalStatus.PENDING,
    val isOnline: Boolean = false,
    val currentLocation: GeoPoint = GeoPoint(),
    val totalEarnings: Double = 0.0
)

data class CartItem(
    val menuItem: MenuItem = MenuItem(),
    val quantity: Int = 1
) {
    val lineTotal: Double get() = menuItem.price * quantity
}

data class Order(
    val id: String = "",
    val customerUid: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val riderUid: String = "",
    val pickup: GeoPoint = GeoPoint(),
    val dropoff: GeoPoint = GeoPoint(),
    val paymentMethod: PaymentMethod = PaymentMethod.MPESA,
    val createdAt: Long = System.currentTimeMillis()
)

data class Ride(
    val id: String = "",
    val customerUid: String = "",
    val driverUid: String = "",
    val driverName: String = "",
    val vehicleType: VehicleType = VehicleType.CAR,
    val pickup: GeoPoint = GeoPoint(),
    val destination: GeoPoint = GeoPoint(),
    val fareEstimate: Double = 0.0,
    val finalFare: Double = 0.0,
    val distanceKm: Double = 0.0,
    val status: RideStatus = RideStatus.REQUESTED,
    val paymentMethod: PaymentMethod = PaymentMethod.MPESA,
    val createdAt: Long = System.currentTimeMillis()
)
