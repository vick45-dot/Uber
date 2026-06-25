package com.safiri.ridedelivery.ui.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.PaymentMethod
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.viewmodel.CartViewModel
import com.safiri.ridedelivery.viewmodel.CatalogViewModel

/**
 * Payment selection. M-Pesa STK push would be triggered server-side
 * (Daraja API) via a Cloud Function — see the implementation guide.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    cartVm: CartViewModel,
    catalogVm: CatalogViewModel
) {
    var method by remember { mutableStateOf(PaymentMethod.MPESA) }
    var phone by remember { mutableStateOf("") }
    val address by cartVm.selectedAddress.collectAsState()
    val items by cartVm.items.collectAsState()
    val restaurants by catalogVm.restaurants.collectAsState()

    val restaurantId = items.firstOrNull()?.menuItem?.restaurantId
    val restaurant = restaurants.find { it.id == restaurantId }

    val isMobileMoney = method in listOf(PaymentMethod.MPESA, PaymentMethod.AIRTEL_MONEY, PaymentMethod.T_KASH)

    Scaffold(topBar = { TopAppBar(title = { Text("Payment") }) }) { pad ->
        LazyColumn(
            Modifier
                .padding(pad)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            address?.let {
                item {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.LocationOn, null, tint = Color(0xFF00A082), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Delivering to:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(it.address, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            item { Text("How would you like to pay?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }

            item { PaymentSectionHeader("Mobile Money") }
            items(listOf(PaymentMethod.MPESA, PaymentMethod.AIRTEL_MONEY, PaymentMethod.T_KASH)) { m ->
                PaymentOptionRow(m, method == m) { method = it }
            }

            if (isMobileMoney) {
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("${method.name.replace("_", " ")} Phone Number") },
                        placeholder = { Text("e.g. 2547XXXXXXXX") },
                        leadingIcon = { Icon(Icons.Rounded.Phone, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            item { PaymentSectionHeader("Cards & Digital Wallets") }
            items(listOf(PaymentMethod.CARD, PaymentMethod.PAYPAL, PaymentMethod.GOOGLE_PAY, PaymentMethod.APPLE_PAY)) { m ->
                PaymentOptionRow(m, method == m) { method = it }
            }

            item { PaymentSectionHeader("Others") }
            items(listOf(PaymentMethod.CASH, PaymentMethod.CRYPTO)) { m ->
                PaymentOptionRow(m, method == m) { method = it }
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                Button(
                    onClick = {
                        if (restaurant != null && address != null) {
                            cartVm.checkout(restaurant, address!!, method) { orderId ->
                                if (orderId != null) {
                                    navController.navigate(Routes.orderTracking(orderId)) {
                                        popUpTo(Routes.HOME)
                                    }
                                }
                            }
                        }
                    },
                    enabled = address != null && (!isMobileMoney || phone.isNotBlank()),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A082))
                ) {
                    val label = when (method) {
                        PaymentMethod.CASH -> "Confirm Order"
                        PaymentMethod.MPESA, PaymentMethod.AIRTEL_MONEY, PaymentMethod.T_KASH -> "Pay with Mobile Money"
                        else -> "Proceed with ${method.name.replace("_", " ")}"
                    }
                    Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun PaymentSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun PaymentOptionRow(
    m: PaymentMethod,
    selected: Boolean,
    onSelect: (PaymentMethod) -> Unit
) {
    val icon = when (m) {
        PaymentMethod.MPESA, PaymentMethod.AIRTEL_MONEY, PaymentMethod.T_KASH -> Icons.Rounded.Smartphone
        PaymentMethod.CARD -> Icons.Rounded.CreditCard
        PaymentMethod.CASH -> Icons.Rounded.Payments
        PaymentMethod.PAYPAL -> Icons.Rounded.AccountBalanceWallet
        PaymentMethod.GOOGLE_PAY, PaymentMethod.APPLE_PAY -> Icons.Rounded.AccountBalance
        PaymentMethod.CRYPTO -> Icons.Rounded.CurrencyBitcoin
    }

    Surface(
        onClick = { onSelect(m) },
        shape = RoundedCornerShape(12.dp),
        border = if (selected) BorderStroke(2.dp, Color(0xFF00A082)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) Color(0xFF00A082).copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (selected) Color(0xFF00A082) else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(16.dp))
            Text(
                text = m.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            RadioButton(selected = selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00A082)))
        }
    }
}
