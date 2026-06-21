package com.safiri.ridedelivery.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safiri.ridedelivery.ui.components.AppBottomBar
import com.safiri.ridedelivery.ui.components.SectionHeader
import com.safiri.ridedelivery.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, catalogVm: CatalogViewModel) {
    var query by remember { mutableStateOf("") }
    val results by catalogVm.searchResults.collectAsState()
    val restaurants = results.first
    val shops = results.second

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search") }) },
        bottomBar = { AppBottomBar(navController) }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; catalogVm.search(it) },
                label = { Text("Food, shops, services…") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            LazyColumn {
                if (restaurants.isNotEmpty()) item { SectionHeader("Restaurants") }
                items(restaurants) { r ->
                    ListItem(
                        headlineContent = { Text(r.name) },
                        supportingContent = { Text(r.category) }
                    )
                }
                if (shops.isNotEmpty()) item { SectionHeader("Shops") }
                items(shops) { s ->
                    ListItem(
                        headlineContent = { Text(s.name) },
                        supportingContent = { Text(s.category) }
                    )
                }
            }
        }
    }
}
