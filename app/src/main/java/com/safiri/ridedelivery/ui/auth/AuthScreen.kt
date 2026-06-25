package com.safiri.ridedelivery.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.data.model.UserRole
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController, authVm: AuthViewModel) {
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.CUSTOMER) }
    var busy by remember { mutableStateOf(false) }
    val error by authVm.error.collectAsState()

    fun routeForRole(r: UserRole) = when (r) {
        UserRole.DRIVER -> Routes.DRIVER_DASH
        UserRole.RIDER -> Routes.RIDER_DASH
        UserRole.RESTAURANT -> Routes.RESTAURANT_DASH
        UserRole.ADMIN -> Routes.ADMIN_DASH
        UserRole.CUSTOMER -> Routes.HOME
    }

    Scaffold(topBar = { TopAppBar(title = { Text(if (isLogin) "Log in" else "Create account") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isLogin) {
                OutlinedTextField(name, { name = it }, label = { Text("Full name") },
                    leadingIcon = { Icon(Icons.Rounded.Person, null) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(phone, { phone = it }, label = { Text("Phone (07…)") },
                    leadingIcon = { Icon(Icons.Rounded.Phone, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
            OutlinedTextField(email, { email = it }, label = { Text("Email") },
                leadingIcon = { Icon(Icons.Rounded.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(password, { password = it }, label = { Text("Password") },
                leadingIcon = { Icon(Icons.Rounded.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

            if (!isLogin) {
                Text("Register as:")
                UserRole.values().forEach { r ->
                    Row(Modifier.fillMaxWidth().selectable(role == r, onClick = { role = r }),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = role == r, onClick = null)
                        Spacer(Modifier.width(8.dp)); Text(r.name)
                    }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    busy = true
                    val cb: (Boolean) -> Unit = { ok ->
                        busy = false
                        if (ok) navController.navigate(routeForRole(role)) {
                            popUpTo(Routes.HOME)
                        }
                    }
                    if (isLogin) authVm.login(email, password) { ok ->
                        busy = false
                        if (ok) navController.popBackStack()
                    } else authVm.register(name, email, phone, password, role, cb)
                },
                enabled = !busy && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text(if (isLogin) "Log in" else "Sign up") }

            TextButton(onClick = { isLogin = !isLogin }) {
                Text(if (isLogin) "New here? Create an account" else "Already have an account? Log in")
            }
        }
    }
}
