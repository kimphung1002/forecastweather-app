package com.example.forecastweather.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(val title: String, val route: String, val icon: ImageVector)

val drawerItems = listOf(
    DrawerItem("Hôm nay", NavigationRoutes.DAILY, Icons.Default.Brightness5),
    DrawerItem("5 ngày", NavigationRoutes.WEEKLY, Icons.Default.DateRange),
    DrawerItem("Cài đặt", NavigationRoutes.SETTINGS, Icons.Default.Settings),
    DrawerItem("Giới thiệu", NavigationRoutes.ABOUT, Icons.Default.Info)
)
