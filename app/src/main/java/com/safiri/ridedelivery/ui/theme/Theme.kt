package com.safiri.ridedelivery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Brand palette — Uber-black + Glovo-green accent.
val BrandGreen = Color(0xFF00A082)
val BrandGreenDark = Color(0xFF00C9A7)
val Ink = Color(0xFF0E0E0E)
val Cloud = Color(0xFFF6F7F9)

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    secondary = Ink,
    background = Cloud,
    surface = Color.White,
    onBackground = Ink,
    onSurface = Ink
)

private val DarkColors = darkColorScheme(
    primary = BrandGreenDark,
    onPrimary = Ink,
    secondary = Color.White,
    background = Color(0xFF0B0B0B),
    surface = Color(0xFF161616),
    onBackground = Color.White,
    onSurface = Color.White
)

private val AppTypography = Typography(
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
)

@Composable
fun RideDeliveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
