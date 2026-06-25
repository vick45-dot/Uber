package com.safiri.ridedelivery.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
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
    val context = LocalContext.current

    val shareAppLambda = {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey! Check out Ride & Delivery app. Book rides and order food easily in Kenya! Download it here: https://github.com/vick45-dot/Uber"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Ride & Delivery App via")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
        bottomBar = { AppBottomBar(navController) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (user == null) {
                Icon(Icons.Rounded.AccountCircle, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outline)
                Text("You're browsing as a guest.", fontWeight = FontWeight.SemiBold)
                Text("Log in to order food, book rides, and save addresses.")
                Button(
                    onClick = { navController.navigate(Routes.AUTH) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Login, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Log in / Sign up")
                }
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
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // App Share Options
            Text("App Settings", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            OutlinedButton(
                onClick = shareAppLambda,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Rounded.Share, null)
                Spacer(Modifier.width(8.dp))
                Text("Share App with Friends")
            }

            if (user != null) {
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = { authVm.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Rounded.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Log out")
                }
            }
        }
    }
}

@Composable
private fun DashButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Rounded.Dashboard, null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
