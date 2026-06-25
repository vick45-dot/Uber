package com.safiri.ridedelivery.ui.food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
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
import com.safiri.ridedelivery.ui.theme.BrandGreen
import com.safiri.ridedelivery.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartVm: CartViewModel) {
    val items by cartVm.items.collectAsState()
    val subtotal by cartVm.subtotal.collectAsState()
    val deliveryFee = 150.0
    val total = subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your cart") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                }
            })
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Column(Modifier.padding(16.dp)) {
                    SummaryRow("Subtotal", subtotal)
                    SummaryRow("Delivery fee", deliveryFee)
                    Divider(Modifier.padding(vertical = 8.dp))
                    SummaryRow("Total", total, bold = true)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { navController.navigate(Routes.PAYMENT) },
                        enabled = items.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Proceed to payment", fontWeight = FontWeight.Bold) }
                }
            }
        }
    ) { pad ->
        if (items.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 48.sp)
                    Text("Your cart is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            return@Scaffold
        }
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            items(items) { ci ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiBadge(ci.menuItem.emoji, size = 48)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(ci.menuItem.name, fontWeight = FontWeight.SemiBold)
                        Text("KES ${ci.menuItem.price.toInt()}", color = BrandGreen,
                            fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(
                            onClick = { cartVm.remove(ci.menuItem.id) },
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) { Icon(Icons.Rounded.Remove, "Remove", tint = MaterialTheme.colorScheme.onSurface) }
                        Text("  ${ci.quantity}  ", fontWeight = FontWeight.Bold)
                        FilledIconButton(
                            onClick = { cartVm.add(ci.menuItem) },
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = BrandGreen)
                        ) { Icon(Icons.Rounded.Add, "Add") }
                    }
                }
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (bold) 16.sp else 14.sp)
        Text("KES ${amount.toInt()}", fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (bold) 16.sp else 14.sp, color = if (bold) BrandGreen else MaterialTheme.colorScheme.onSurface)
    }
}
