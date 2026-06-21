package com.safiri.ridedelivery.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.UserRole
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.ui.components.AppBottomBar
import com.safiri.ridedelivery.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, authVm: AuthViewModel) {
    val user by authVm.user.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
        bottomBar = { AppBottomBar(navController) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (user == null) {
                Text("You're browsing as a guest.", fontWeight = FontWeight.SemiBold)
                Text("Log in to order food, book rides, and save addresses.")
                Button(onClick = { navController.navigate(Routes.AUTH) },
                    modifier = Modifier.fillMaxWidth()) { Text("Log in / Sign up") }
            } else {
                val u = user!!
                Text(u.name, style = MaterialTheme.typography.titleLarge)
                Text(u.email); Text(u.phone)
                AssistChip(onClick = {}, label = { Text("Role: ${u.role}") })
                Divider()
                // Role-based dashboard shortcuts.
                when (u.role) {
                    UserRole.DRIVER -> DashButton("Driver dashboard") { navController.navigate(Routes.DRIVER_DASH) }
                    UserRole.RIDER -> DashButton("Rider dashboard") { navController.navigate(Routes.RIDER_DASH) }
                    UserRole.RESTAURANT -> DashButton("Restaurant dashboard") { navController.navigate(Routes.RESTAURANT_DASH) }
                    UserRole.ADMIN -> DashButton("Admin dashboard") { navController.navigate(Routes.ADMIN_DASH) }
                    UserRole.CUSTOMER -> {}
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = { authVm.logout() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Log out")
                }
            }
        }
    }
}

@Composable
private fun DashButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(label) }
}
