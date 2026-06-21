package com.safiri.ridedelivery.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.ui.theme.BrandGreen
import com.safiri.ridedelivery.ui.theme.BrandGreenDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1400)
        navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
    }

    // Gentle bobbing animation for the scooter.
    val transition = rememberInfiniteTransition(label = "bob")
    val offset by transition.animateFloat(
        initialValue = 0f, targetValue = -12f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "y"
    )

    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(BrandGreen, BrandGreenDark))
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛵", fontSize = 72.sp, modifier = Modifier.offset(y = offset.dp))
            Spacer(Modifier.height(16.dp))
            Text("Ride & Delivery", color = Color.White,
                fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text("Move. Eat. Deliver.", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
        }
    }
}
