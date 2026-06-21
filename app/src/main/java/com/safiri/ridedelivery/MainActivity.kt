package com.safiri.ridedelivery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.safiri.ridedelivery.navigation.NavGraph
import com.safiri.ridedelivery.ui.theme.RideDeliveryTheme
import com.safiri.ridedelivery.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RideDeliveryTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // Shared ViewModels so cart/auth/ride state survives navigation.
                    val authVm: AuthViewModel = viewModel()
                    val catalogVm: CatalogViewModel = viewModel()
                    val cartVm: CartViewModel = viewModel()
                    val rideVm: RideViewModel = viewModel()
                    NavGraph(navController, authVm, catalogVm, cartVm, rideVm)
                }
            }
        }
    }
}
