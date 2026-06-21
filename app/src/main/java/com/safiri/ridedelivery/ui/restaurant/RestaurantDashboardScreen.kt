package com.safiri.ridedelivery.ui.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.MenuItem
import com.safiri.ridedelivery.viewmodel.RestaurantViewModel

/** Restaurant owners register the restaurant, manage the menu, and see orders. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(navController: NavController, vm: RestaurantViewModel) {
    var restaurantId by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    val orders by vm.orders.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Restaurant dashboard") }) }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Add menu item", fontWeight = FontWeight.Bold)
                        OutlinedTextField(restaurantId, { restaurantId = it },
                            label = { Text("Restaurant ID") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(itemName, { itemName = it },
                            label = { Text("Item name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(itemPrice, { itemPrice = it },
                            label = { Text("Price (KES)") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = {
                            vm.saveItem(MenuItem(
                                restaurantId = restaurantId,
                                name = itemName,
                                price = itemPrice.toDoubleOrNull() ?: 0.0
                            ))
                            itemName = ""; itemPrice = ""
                        }, modifier = Modifier.fillMaxWidth()) { Text("Add to menu") }
                        OutlinedButton(onClick = { vm.loadOrders(restaurantId) },
                            modifier = Modifier.fillMaxWidth()) { Text("Load orders") }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Orders", fontWeight = FontWeight.Bold)
            }
            items(orders) { o ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Order ${o.id.take(6)} — ${o.status}")
                        Text("KES ${o.total.toInt()} • ${o.items.sumOf { it.quantity }} items")
                    }
                }
            }
        }
    }
}
