package com.safiri.ridedelivery.ui.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.PaymentMethod

/**
 * Payment selection. M-Pesa STK push would be triggered server-side
 * (Daraja API) via a Cloud Function — see the implementation guide.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController) {
    var method by remember { mutableStateOf(PaymentMethod.MPESA) }
    var phone by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Payment") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Select payment method", style = MaterialTheme.typography.titleLarge)
            PaymentMethod.values().forEach { m ->
                Row(Modifier.fillMaxWidth().selectable(method == m, onClick = { method = m }),
                    verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = method == m, onClick = null)
                    Spacer(Modifier.width(8.dp)); Text(m.name)
                }
            }
            if (method == PaymentMethod.MPESA) {
                OutlinedTextField(phone, { phone = it }, label = { Text("M-Pesa phone (2547…)") },
                    modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                // TODO: call backend to initiate STK push / card charge.
                navController.popBackStack()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (method == PaymentMethod.MPESA) "Pay with M-Pesa" else "Confirm payment")
            }
        }
    }
}
