package com.safiri.ridedelivery.ui.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.ApprovalStatus
import com.safiri.ridedelivery.data.model.VehicleType
import com.safiri.ridedelivery.data.model.Driver
import com.safiri.ridedelivery.viewmodel.DriverViewModel

/**
 * Shared by Driver and Rider (rider = food courier). Handles vehicle registration,
 * approval gating, online toggle, incoming requests, and earnings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(navController: NavController, vm: DriverViewModel, rider: Boolean = false) {
    LaunchedEffect(Unit) { vm.load() }
    val profile by vm.profile.collectAsState()
    val requests by vm.requests.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(if (rider) "Rider dashboard" else "Driver dashboard") }) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            val d = profile
            if (d == null) {
                Text("Loading profile…"); return@Column
            }

            when (d.approval) {
                ApprovalStatus.PENDING -> ApprovalBanner("Your account is pending admin approval.")
                ApprovalStatus.REJECTED -> ApprovalBanner("Your registration was rejected. Contact support.")
                ApprovalStatus.APPROVED -> {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(if (d.isOnline) "You're online" else "You're offline",
                            fontWeight = FontWeight.SemiBold)
                        Switch(checked = d.isOnline, onCheckedChange = { vm.toggleOnline(it) })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            VehicleRegistrationCard(d) { vm.saveVehicle(it) }

            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Earnings", fontWeight = FontWeight.Bold)
                    Text("KES ${d.totalEarnings.toInt()}")
                    Text("Rating: ★ ${"%.1f".format(d.rating)}")
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Incoming requests", fontWeight = FontWeight.Bold)
            LazyColumn {
                items(requests) { ride ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${ride.vehicleType} • KES ${ride.fareEstimate.toInt()}")
                            Text("From: ${ride.pickup.address}")
                            Text("To: ${ride.destination.address}")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { vm.respond(ride, true) }) { Text("Accept") }
                                OutlinedButton(onClick = { vm.respond(ride, false) }) { Text("Reject") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ApprovalBanner(text: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Text(text, Modifier.padding(16.dp))
    }
}

@Composable
private fun VehicleRegistrationCard(driver: Driver, onSave: (Driver) -> Unit) {
    var model by remember { mutableStateOf(driver.vehicleModel) }
    var plate by remember { mutableStateOf(driver.plateNumber) }
    var type by remember { mutableStateOf(driver.vehicleType) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Vehicle details", fontWeight = FontWeight.Bold)
            OutlinedTextField(model, { model = it }, label = { Text("Model (e.g. Toyota Vitz)") },
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(plate, { plate = it }, label = { Text("Plate number") },
                modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                VehicleType.values().forEach { t ->
                    FilterChip(selected = type == t, onClick = { type = t },
                        label = { Text(t.name.take(4)) })
                }
            }
            Button(onClick = {
                onSave(driver.copy(vehicleModel = model, plateNumber = plate, vehicleType = type))
            }, modifier = Modifier.fillMaxWidth()) { Text("Save vehicle") }
        }
    }
}
