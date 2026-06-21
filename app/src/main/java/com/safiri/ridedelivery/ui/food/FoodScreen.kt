package com.safiri.ridedelivery.ui.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.safiri.ridedelivery.navigation.Routes
import com.safiri.ridedelivery.ui.components.*
import com.safiri.ridedelivery.ui.home.RestaurantRow
import com.safiri.ridedelivery.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(navController: NavController, catalogVm: CatalogViewModel) {
    LaunchedEffect(Unit) { catalogVm.loadHome() }
    val restaurants by catalogVm.restaurants.collectAsState()
    val shops by catalogVm.shops.collectAsState()

    Scaffold(bottomBar = { AppBottomBar(navController) }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            item { GradientHero("Hungry?", "Food & shops in Nakuru") }
            item { SectionHeader("Restaurants") }
            items(restaurants) { r ->
                RestaurantRow(r) { navController.navigate(Routes.restaurantMenu(r.id)) }
            }
            item { SectionHeader("Shops") }
            items(shops) { s ->
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        EmojiBadge(s.emoji, size = 56)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(s.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(s.category, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        RatingChip(s.rating, s.etaMinutes)
                    }
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}
