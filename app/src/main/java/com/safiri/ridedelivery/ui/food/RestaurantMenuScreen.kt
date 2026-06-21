package com.safiri.ridedelivery.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.ui.components.EmojiBadge
import com.safiri.ridedelivery.ui.components.LoginRequiredDialog
import com.safiri.ridedelivery.ui.components.PopularTag
import com.safiri.ridedelivery.ui.theme.BrandGreen
import com.safiri.ridedelivery.viewmodel.AuthViewModel
import com.safiri.ridedelivery.viewmodel.CartViewModel
import com.safiri.ridedelivery.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuScreen(
    navController: NavController,
    catalogVm: CatalogViewModel,
    cartVm: CartViewModel,
    authVm: AuthViewModel,
    restaurantId: String
) {
    LaunchedEffect(restaurantId) { catalogVm.loadMenu(restaurantId) }
    val menu by catalogVm.menu.collectAsState()
    val restaurants by catalogVm.restaurants.collectAsState()
    val cart by cartVm.items.collectAsState()
    var showLogin by remember { mutableStateOf(false) }

    val restaurant = restaurants.find { it.id == restaurantId }
    // Group menu items by their food category for a sectioned menu.
    val grouped = menu.groupBy { it.foodCategory }
    val categoryOrder = listOf("Mains", "Sides", "Dessert", "Drinks")
    val orderedKeys = grouped.keys.sortedBy { categoryOrder.indexOf(it).let { i -> if (i < 0) 99 else i } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.name ?: "Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (cart.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (authVm.isLoggedIn) navController.navigate(Routes.CART)
                        else showLogin = true
                    },
                    containerColor = BrandGreen,
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    text = { Text("View cart • ${cart.sumOf { it.quantity }}") }
                )
            }
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            // Restaurant banner
            restaurant?.let { r ->
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmojiBadge(r.emoji, size = 64)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(r.description, fontWeight = FontWeight.Medium)
                            Text("⭐ ${"%.1f".format(r.rating)} (${r.ratingCount}) • ${r.etaMinutes} min • 🛵 KES ${r.deliveryFee.toInt()}",
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Divider()
                }
            }
            // Sectioned menu by category
            orderedKeys.forEach { cat ->
                item {
                    Text(cat, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp))
                }
                items(grouped[cat].orEmpty()) { item ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmojiBadge(item.emoji, size = 48)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, fontWeight = FontWeight.SemiBold)
                                if (item.popular) { Spacer(Modifier.width(6.dp)); PopularTag() }
                            }
                            Text(item.description, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            Text("KES ${item.price.toInt()}", fontWeight = FontWeight.Bold,
                                color = BrandGreen, fontSize = 14.sp)
                        }
                        FilledIconButton(
                            onClick = { cartVm.add(item) },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = BrandGreen)
                        ) { Icon(Icons.Default.Add, "Add ${item.name}") }
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showLogin) {
        LoginRequiredDialog(
            onLogin = { showLogin = false; navController.navigate(Routes.AUTH) },
            onDismiss = { showLogin = false }
        )
    }
}
