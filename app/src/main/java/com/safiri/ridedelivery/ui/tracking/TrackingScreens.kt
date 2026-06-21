package com.safiri.ridedelivery.ui.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.safiri.ridedelivery.data.repository.OrderRepository
import com.safiri.ridedelivery.viewmodel.RideViewModel

/**
 * Live order tracking. Observes the order document and re-centres the map
 * on the rider's position as Firestore pushes updates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(navController: NavController, orderId: String) {
    val repo = remember { OrderRepository() }
    val order by repo.observeOrder(orderId).collectAsState(initial = null)

    val dropoff = order?.dropoff
    val pos = LatLng(dropoff?.lat ?: -0.3031, dropoff?.lng ?: 36.0800)
    val cam = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(pos, 14f) }

    Scaffold(topBar = { TopAppBar(title = { Text("Order tracking") }) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            order?.let {
                Card(Modifier.fillMaxWidth().padding(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(it.restaurantName, fontWeight = FontWeight.Bold)
                        Text("Status: ${it.status}")
                        Text("Total: KES ${it.total.toInt()}")
                    }
                }
            }
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cam) {
                Marker(state = MarkerState(pos), title = "Delivery to")
            }
        }
    }
}

/** Live ride tracking — driver marker updates as the ride document changes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideTrackingScreen(navController: NavController, rideVm: RideViewModel, rideId: String) {
    val ride by rideVm.observeRide(rideId).collectAsState(initial = null)
    val p = ride?.pickup
    val pos = LatLng(p?.lat ?: -0.3031, p?.lng ?: 36.0800)
    val cam = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(pos, 14f) }

    Scaffold(topBar = { TopAppBar(title = { Text("Ride tracking") }) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            ride?.let {
                Card(Modifier.fillMaxWidth().padding(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Driver: ${it.driverName.ifBlank { "Searching…" }}", fontWeight = FontWeight.Bold)
                        Text("Status: ${it.status}")
                        Text("Fare: KES ${it.fareEstimate.toInt()}")
                    }
                }
            }
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cam) {
                ride?.pickup?.let { Marker(state = MarkerState(LatLng(it.lat, it.lng)), title = "Pickup") }
                ride?.destination?.let { Marker(state = MarkerState(LatLng(it.lat, it.lng)), title = "Destination") }
            }
        }
    }
}
