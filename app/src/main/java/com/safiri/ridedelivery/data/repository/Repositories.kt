package com.safiri.ridedelivery.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.safiri.ridedelivery.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Collection names in one place keeps Firestore references consistent.
 */
object Col {
    const val USERS = "users"
    const val RESTAURANTS = "restaurants"
    const val MENU = "menuItems"
    const val SHOPS = "shops"
    const val DRIVERS = "drivers"
    const val ORDERS = "orders"
    const val RIDES = "rides"
}

/**
 * Every repository checks AppConfig.USE_FIREBASE:
 *   - false -> in-memory MockData (offline, no billing)
 *   - true  -> live Firebase
 * ViewModels are unaware of which backend is active.
 */

class AuthRepository(
    private val auth: FirebaseAuth? = if (AppConfig.USE_FIREBASE) FirebaseAuth.getInstance() else null,
    private val db: FirebaseFirestore? = if (AppConfig.USE_FIREBASE) FirebaseFirestore.getInstance() else null
) {
    val currentUid: String?
        get() = if (AppConfig.USE_FIREBASE) auth?.currentUser?.uid else MockData.currentUser?.uid

    val isLoggedIn: Boolean
        get() = if (AppConfig.USE_FIREBASE) auth?.currentUser != null else MockData.currentUser != null

    suspend fun register(name: String, email: String, phone: String, password: String, role: UserRole): Result<AppUser> {
        if (!AppConfig.USE_FIREBASE) {
            val uid = MockData.nextId("u")
            val user = AppUser(uid, name, email, phone, role)
            MockData.users[uid] = user
            MockData.currentUser = user
            // Mock driver/rider get an auto-approved profile so dashboards work immediately.
            if (role == UserRole.DRIVER || role == UserRole.RIDER) {
                MockData.drivers.add(Driver(uid = uid, name = name, phone = phone,
                    approval = ApprovalStatus.APPROVED))
            }
            return Result.success(user)
        }
        return runCatching {
            val res = auth!!.createUserWithEmailAndPassword(email, password).await()
            val uid = res.user!!.uid
            val user = AppUser(uid, name, email, phone, role)
            db!!.collection(Col.USERS).document(uid).set(user).await()
            if (role == UserRole.DRIVER || role == UserRole.RIDER) {
                db.collection(Col.DRIVERS).document(uid)
                    .set(Driver(uid = uid, name = name, phone = phone)).await()
            }
            user
        }
    }

    suspend fun login(email: String, password: String): Result<AppUser> {
        if (!AppConfig.USE_FIREBASE) {
            // Offline: match by email, or fabricate a customer so any login works in a demo.
            val existing = MockData.users.values.find { it.email.equals(email, ignoreCase = true) }
            val user = existing ?: AppUser(
                uid = MockData.nextId("u"),
                name = email.substringBefore('@').replaceFirstChar { it.uppercase() },
                email = email, role = UserRole.CUSTOMER
            ).also { MockData.users[it.uid] = it }
            MockData.currentUser = user
            return Result.success(user)
        }
        return runCatching {
            val res = auth!!.signInWithEmailAndPassword(email, password).await()
            db!!.collection(Col.USERS).document(res.user!!.uid).get().await().toObject<AppUser>()
                ?: error("Profile not found")
        }
    }

    suspend fun currentUser(): AppUser? {
        if (!AppConfig.USE_FIREBASE) return MockData.currentUser
        return currentUid?.let { db!!.collection(Col.USERS).document(it).get().await().toObject<AppUser>() }
    }

    fun logout() {
        if (!AppConfig.USE_FIREBASE) { MockData.currentUser = null; return }
        auth!!.signOut()
    }
}

class CatalogRepository(
    private val db: FirebaseFirestore? = if (AppConfig.USE_FIREBASE) FirebaseFirestore.getInstance() else null
) {
    suspend fun restaurants(): List<Restaurant> {
        if (!AppConfig.USE_FIREBASE) { delay(200); return MockData.restaurants }
        return db!!.collection(Col.RESTAURANTS).get().await().toObjects()
    }

    suspend fun shops(): List<Shop> {
        if (!AppConfig.USE_FIREBASE) return MockData.shops
        return db!!.collection(Col.SHOPS).get().await().toObjects()
    }

    suspend fun menu(restaurantId: String): List<MenuItem> {
        if (!AppConfig.USE_FIREBASE) return MockData.menuItems.filter { it.restaurantId == restaurantId }
        return db!!.collection(Col.MENU).whereEqualTo("restaurantId", restaurantId).get().await().toObjects()
    }

    suspend fun availableDrivers(): List<Driver> {
        if (!AppConfig.USE_FIREBASE)
            return MockData.drivers.filter { it.isOnline && it.approval == ApprovalStatus.APPROVED }
        return db!!.collection(Col.DRIVERS)
            .whereEqualTo("isOnline", true)
            .whereEqualTo("approval", ApprovalStatus.APPROVED.name)
            .get().await().toObjects()
    }

    suspend fun search(query: String): Pair<List<Restaurant>, List<Shop>> {
        val q = query.trim().lowercase()
        val r = restaurants().filter { it.name.lowercase().contains(q) || it.category.lowercase().contains(q) }
        val s = shops().filter { it.name.lowercase().contains(q) || it.category.lowercase().contains(q) }
        return r to s
    }
}

class OrderRepository(
    private val db: FirebaseFirestore? = if (AppConfig.USE_FIREBASE) FirebaseFirestore.getInstance() else null
) {
    suspend fun placeOrder(order: Order): Result<String> {
        if (!AppConfig.USE_FIREBASE) {
            val id = MockData.nextId("o")
            MockData.orders.add(order.copy(id = id))
            return Result.success(id)
        }
        return runCatching {
            val ref = db!!.collection(Col.ORDERS).document()
            ref.set(order.copy(id = ref.id)).await()
            ref.id
        }
    }

    fun observeOrder(orderId: String): Flow<Order?> {
        if (!AppConfig.USE_FIREBASE) return flow { emit(MockData.orders.find { it.id == orderId }) }
        return callbackFlow {
            val reg = db!!.collection(Col.ORDERS).document(orderId)
                .addSnapshotListener { snap, _ -> trySend(snap?.toObject<Order>()) }
            awaitClose { reg.remove() }
        }
    }

    suspend fun customerOrders(uid: String): List<Order> {
        if (!AppConfig.USE_FIREBASE) return MockData.orders.filter { it.customerUid == uid }
        return db!!.collection(Col.ORDERS).whereEqualTo("customerUid", uid).get().await().toObjects()
    }

    suspend fun updateStatus(orderId: String, status: OrderStatus) {
        if (!AppConfig.USE_FIREBASE) {
            val i = MockData.orders.indexOfFirst { it.id == orderId }
            if (i >= 0) MockData.orders[i] = MockData.orders[i].copy(status = status)
            return
        }
        db!!.collection(Col.ORDERS).document(orderId).update("status", status.name).await()
    }
}

class RideRepository(
    private val db: FirebaseFirestore? = if (AppConfig.USE_FIREBASE) FirebaseFirestore.getInstance() else null
) {
    suspend fun requestRide(ride: Ride): Result<String> {
        if (!AppConfig.USE_FIREBASE) {
            val id = MockData.nextId("ride")
            // Auto-assign the first matching online driver so tracking has data.
            val driver = MockData.drivers.firstOrNull { it.isOnline && it.vehicleType == ride.vehicleType }
                ?: MockData.drivers.firstOrNull { it.isOnline }
            MockData.rides.add(ride.copy(
                id = id,
                driverUid = driver?.uid ?: "",
                driverName = driver?.name ?: "",
                status = if (driver != null) RideStatus.ACCEPTED else RideStatus.REQUESTED
            ))
            return Result.success(id)
        }
        return runCatching {
            val ref = db!!.collection(Col.RIDES).document()
            ref.set(ride.copy(id = ref.id)).await()
            ref.id
        }
    }

    fun observeRide(rideId: String): Flow<Ride?> {
        if (!AppConfig.USE_FIREBASE) return flow { emit(MockData.rides.find { it.id == rideId }) }
        return callbackFlow {
            val reg = db!!.collection(Col.RIDES).document(rideId)
                .addSnapshotListener { snap, _ -> trySend(snap?.toObject<Ride>()) }
            awaitClose { reg.remove() }
        }
    }

    fun pendingRequests(): Flow<List<Ride>> {
        if (!AppConfig.USE_FIREBASE)
            return flow { emit(MockData.rides.filter { it.status == RideStatus.REQUESTED }) }
        return callbackFlow {
            val reg = db!!.collection(Col.RIDES)
                .whereEqualTo("status", RideStatus.REQUESTED.name)
                .addSnapshotListener { snap, _ -> trySend(snap?.toObjects<Ride>() ?: emptyList()) }
            awaitClose { reg.remove() }
        }
    }

    suspend fun respondToRide(rideId: String, driver: Driver, accept: Boolean) {
        if (!AppConfig.USE_FIREBASE) {
            val i = MockData.rides.indexOfFirst { it.id == rideId }
            if (i >= 0) MockData.rides[i] = MockData.rides[i].copy(
                status = if (accept) RideStatus.ACCEPTED else RideStatus.CANCELLED,
                driverUid = if (accept) driver.uid else "",
                driverName = if (accept) driver.name else ""
            )
            return
        }
        val status = if (accept) RideStatus.ACCEPTED else RideStatus.CANCELLED
        val updates = mutableMapOf<String, Any>("status" to status.name)
        if (accept) { updates["driverUid"] = driver.uid; updates["driverName"] = driver.name }
        db!!.collection(Col.RIDES).document(rideId).update(updates).await()
    }
}

class DriverRepository(
    private val db: FirebaseFirestore? = if (AppConfig.USE_FIREBASE) FirebaseFirestore.getInstance() else null
) {
    suspend fun saveVehicle(driver: Driver) {
        if (!AppConfig.USE_FIREBASE) {
            val i = MockData.drivers.indexOfFirst { it.uid == driver.uid }
            if (i >= 0) MockData.drivers[i] = driver else MockData.drivers.add(driver)
            return
        }
        db!!.collection(Col.DRIVERS).document(driver.uid).set(driver).await()
    }

    suspend fun setOnline(uid: String, online: Boolean) {
        if (!AppConfig.USE_FIREBASE) {
            val i = MockData.drivers.indexOfFirst { it.uid == uid }
            if (i >= 0) MockData.drivers[i] = MockData.drivers[i].copy(isOnline = online)
            return
        }
        db!!.collection(Col.DRIVERS).document(uid).update("isOnline", online).await()
    }

    suspend fun updateLocation(uid: String, point: GeoPoint) {
        if (!AppConfig.USE_FIREBASE) {
            val i = MockData.drivers.indexOfFirst { it.uid == uid }
            if (i >= 0) MockData.drivers[i] = MockData.drivers[i].copy(currentLocation = point)
            return
        }
        db!!.collection(Col.DRIVERS).document(uid).update("currentLocation", point).await()
    }

    suspend fun driver(uid: String): Driver? {
        if (!AppConfig.USE_FIREBASE) return MockData.drivers.find { it.uid == uid }
        return db!!.collection(Col.DRIVERS).document(uid).get().await().toObject<Driver>()
    }

    suspend fun pendingApprovals(): List<Driver> {
        if (!AppConfig.USE_FIREBASE)
            return MockData.drivers.filter { it.approval == ApprovalStatus.PENDING }
        return db!!.collection(Col.DRIVERS)
            .whereEqualTo("approval", ApprovalStatus.PENDING.name).get().await().toObjects()
    }

    suspend fun setApproval(uid: String, status: ApprovalStatus) {
        if (!AppConfig.USE_FIREBASE) {
            val i = MockData.drivers.indexOfFirst { it.uid == uid }
            if (i >= 0) MockData.drivers[i] = MockData.drivers[i].copy(approval = status)
            return
        }
        db!!.collection(Col.DRIVERS).document(uid).update("approval", status.name).await()
    }
}

class RestaurantRepository(
    private val db: FirebaseFirestore? = if (AppConfig.USE_FIREBASE) FirebaseFirestore.getInstance() else null
) {
    suspend fun saveRestaurant(r: Restaurant): String {
        if (!AppConfig.USE_FIREBASE) {
            val id = if (r.id.isBlank()) MockData.nextId("r") else r.id
            val i = MockData.restaurants.indexOfFirst { it.id == id }
            val withId = r.copy(id = id)
            if (i >= 0) MockData.restaurants[i] = withId else MockData.restaurants.add(withId)
            return id
        }
        val ref = if (r.id.isBlank()) db!!.collection(Col.RESTAURANTS).document()
                  else db!!.collection(Col.RESTAURANTS).document(r.id)
        ref.set(r.copy(id = ref.id)).await()
        return ref.id
    }

    suspend fun saveMenuItem(item: MenuItem) {
        if (!AppConfig.USE_FIREBASE) {
            val id = if (item.id.isBlank()) MockData.nextId("m") else item.id
            val i = MockData.menuItems.indexOfFirst { it.id == id }
            val withId = item.copy(id = id)
            if (i >= 0) MockData.menuItems[i] = withId else MockData.menuItems.add(withId)
            return
        }
        val ref = if (item.id.isBlank()) db!!.collection(Col.MENU).document()
                  else db!!.collection(Col.MENU).document(item.id)
        ref.set(item.copy(id = ref.id)).await()
    }

    suspend fun restaurantOrders(restaurantId: String): List<Order> {
        if (!AppConfig.USE_FIREBASE) return MockData.orders.filter { it.restaurantId == restaurantId }
        return db!!.collection(Col.ORDERS).whereEqualTo("restaurantId", restaurantId).get().await().toObjects()
    }
}
