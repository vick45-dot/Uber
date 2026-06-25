package com.safiri.ridedelivery.ui.ride

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.GeoPoint
import com.safiri.ridedelivery.data.model.PaymentMethod
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.ui.components.AppBottomBar
import com.safiri.ridedelivery.ui.components.GradientHero
import com.safiri.ridedelivery.ui.components.LoginRequiredDialog
import com.safiri.ridedelivery.ui.theme.BrandGreen
import com.safiri.ridedelivery.util.FareCalculator
import com.safiri.ridedelivery.util.VehicleInfo
import com.safiri.ridedelivery.viewmodel.AuthViewModel
import com.safiri.ridedelivery.viewmodel.RideViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(navController: NavController, rideVm: RideViewModel, authVm: AuthViewModel) {
    val vehicle by rideVm.vehicleType.collectAsState()
    var pickupText by remember { mutableStateOf("") }
    var destText by remember { mutableStateOf("") }
    var showLogin by remember { mutableStateOf(false) }

    // Demo coordinates around Nakuru; replace with Google Places later.
    val pickupPoint = GeoPoint(-0.3031, 36.0800, pickupText.ifBlank { "Pickup" })
    val destPoint = GeoPoint(-0.2800, 36.0660, destText.ifBlank { "Destination" })
    val ready = pickupText.isNotBlank() && destText.isNotBlank()

    LaunchedEffect(pickupText, destText) {
        rideVm.pickup.value = pickupPoint
        rideVm.destination.value = destPoint
        rideVm.recomputeFare()
    }

    Scaffold(bottomBar = { AppBottomBar(navController) }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            item { GradientHero("Where to?", "Book a ride in Nakuru") }
            // Location inputs
            item {
                Card(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = pickupText, onValueChange = { pickupText = it },
                            label = { Text("Pickup location") },
                            leadingIcon = { Icon(Icons.Rounded.MyLocation, null, tint = BrandGreen) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = destText, onValueChange = { destText = it },
                            label = { Text("Destination") },
                            leadingIcon = { Icon(Icons.Rounded.LocationOn, null, tint = Color(0xFFE8590C)) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                    }
                }
            }
            item {
                Text("Choose your ride", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp))
            }
            // Visual vehicle cards with live per-type fare
            items(VehicleInfo.all.size) { idx ->
                val meta = VehicleInfo.all[idx]
                val fare = if (ready) FareCalculator.estimate(meta.type, pickupPoint, destPoint) else 0.0
                val selected = vehicle == meta.type
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)
                        .clickable { rideVm.vehicleType.value = meta.type; rideVm.recomputeFare() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) BrandGreen.copy(alpha = 0.10f)
                                         else MaterialTheme.colorScheme.surface
                    ),
                    border = if (selected) BorderStroke(2.dp, BrandGreen) else null
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(meta.emoji, fontSize = 34.sp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(meta.label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${meta.capacity} • ${meta.tagline}", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(if (fare > 0) "KES ${fare.toInt()}" else "—",
                            fontWeight = FontWeight.Bold, color = BrandGreen, fontSize = 16.sp)
                    }
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
            item {
                Button(
                    onClick = {
                        if (!authVm.isLoggedIn) { showLogin = true; return@Button }
                        rideVm.bookRide(PaymentMethod.MPESA) { id ->
                            id?.let { navController.navigate(Routes.rideTracking(it)) }
                        }
                    },
                    enabled = ready,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (ready) "Confirm ${VehicleInfo.of(vehicle).label}" else "Enter pickup & destination",
                        fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }

    if (showLogin) {
        LoginRequiredDialog(
            onLogin = { showLogin = false; navController.navigate(Routes.AUTH) },
            onDismiss = { showLogin = false }
        )
    }
}
