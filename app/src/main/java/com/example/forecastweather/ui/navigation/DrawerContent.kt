package com.example.forecastweather.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val isDarkTheme = isSystemInDarkTheme()

    val gradientColors = if (isDarkTheme) {
        listOf(
            Color(0xFF0D47A1), // Navy
            Color(0xFF512DA8), // Indigo
            Color(0xFF673AB7)  // Purple
        )
    } else {
        listOf(
            Color(0xFF0D47A1), // Navy
            Color(0xFF00B0FF), // Azure Blue
            Color(0xFFE1F5FE)  // Light Cyan
        )
    }

    val highlightBrush = Brush.horizontalGradient(gradientColors)

    ModalDrawerSheet {
        Text(
            text = "Weather App",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(24.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        drawerItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = isSelected,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .then(
                        if (isSelected) Modifier.background(
                            brush = highlightBrush,
                            shape = RoundedCornerShape(12.dp)
                        ) else Modifier
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.Transparent,
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
    }
}
