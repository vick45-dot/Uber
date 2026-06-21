package com.safiri.ridedelivery.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.viewmodel.AdminViewModel

/** Admin approves drivers/riders and oversees the platform. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController, vm: AdminViewModel) {
    LaunchedEffect(Unit) { vm.load() }
    val pending by vm.pending.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Admin dashboard") }) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Text("Pending approvals (${pending.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(pending) { d ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(d.name.ifBlank { d.uid.take(8) }, fontWeight = FontWeight.SemiBold)
                            Text("${d.vehicleType} • ${d.plateNumber.ifBlank { "no plate" }}")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { vm.decide(d.uid, true) }) { Text("Approve") }
                                OutlinedButton(onClick = { vm.decide(d.uid, false) }) { Text("Reject") }
                            }
                        }
                    }
                }
            }
        }
    }
}
