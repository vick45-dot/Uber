package com.safiri.ridedelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.ui.theme.BrandGreen
import com.safiri.ridedelivery.ui.theme.BrandGreenDark

/** Bottom navigation shared across primary tabs. */
data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Rounded.Home),
    BottomItem(Routes.FOOD, "Food", Icons.Rounded.Restaurant),
    BottomItem(Routes.RIDE, "Ride", Icons.Rounded.DirectionsCar),
    BottomItem(Routes.SEARCH, "Search", Icons.Rounded.Search),
    BottomItem(Routes.PROFILE, "Profile", Icons.Rounded.Person)
)

@Composable
fun AppBottomBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    NavigationBar(tonalElevation = 8.dp) {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = current == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME)
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 11.sp) }
            )
        }
    }
}

/** Gradient hero header used at the top of Home. */
@Composable
fun GradientHero(
    greeting: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(BrandGreen, BrandGreenDark)),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 28.dp)
    ) {
        Column {
            Text(greeting, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

/** Round emoji badge used on cards instead of network images (offline-friendly). */
@Composable
fun EmojiBadge(emoji: String, size: Int = 56, bg: Color = BrandGreen.copy(alpha = 0.12f)) {
    Box(
        Modifier.size(size.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size * 0.5).sp)
    }
}

/** Horizontal scrolling category chips. */
@Composable
fun CategoryRow(
    categories: List<Pair<String, String>>,   // emoji to label
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (emoji, label) ->
            val isSel = label == selected
            FilterChip(
                selected = isSel,
                onClick = { onSelect(label) },
                label = { Text("$emoji  $label") },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandGreen,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

/** "Login required" dialog for protected actions. */
@Composable
fun LoginRequiredDialog(onLogin: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onLogin) { Text("Log in / Sign up") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Keep browsing") } },
        icon = { Icon(Icons.Rounded.Lock, null, tint = BrandGreen) },
        title = { Text("Login required") },
        text = { Text("Please log in to continue. You can keep browsing freely without an account.") }
    )
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (action != null && onAction != null) {
            Text(action, color = BrandGreen, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onAction))
        }
    }
}

/** Compact rating + ETA pill. */
@Composable
fun RatingChip(rating: Double, eta: Int) {
    Surface(
        shape = RoundedCornerShape(50),
        color = BrandGreen.copy(alpha = 0.12f)
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("${"%.1f".format(rating)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(" • $eta min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Price-level dots ($, $$, $$$). */
@Composable
fun PriceLevel(level: Int) {
    Text("KES".plus(" •".repeat(level.coerceIn(1,4))),
        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** Small "Popular" tag. */
@Composable
fun PopularTag() {
    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFF1E6)) {
        Row(Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("🔥", fontSize = 10.sp)
            Spacer(Modifier.width(2.dp))
            Text("Popular", fontSize = 10.sp, color = Color(0xFFE8590C), fontWeight = FontWeight.SemiBold)
        }
    }
}
