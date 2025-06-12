package com.example.forecastweather.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomBarContent(
    navController: NavController,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
//            label = { Text("Menu") },
            selected = false,
            onClick = onMenuClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
//            label = { Text("Tìm") },
            selected = false,
            onClick = onSearchClick
        )
    }
}
