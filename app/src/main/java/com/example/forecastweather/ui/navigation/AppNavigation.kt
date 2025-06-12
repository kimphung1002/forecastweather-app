package com.example.forecastweather.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.forecastweather.ui.daily.DailyWeatherViewModel
import com.example.forecastweather.ui.search.SearchViewModel
import com.example.forecastweather.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: DailyWeatherViewModel,
    settingsViewModel: SettingsViewModel,
    searchViewModel: SearchViewModel  // ✅ Thêm kiểu
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(navController, drawerState, scope) }
    ) {
        Scaffold(
            bottomBar = {
                BottomBarContent(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { navController.navigate(NavigationRoutes.SEARCH) }
                )
            }
        ) { paddingValues ->
            NavigationGraph(
                navController = navController,
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                searchViewModel = searchViewModel, // ✅ Truyền thêm ở đây nếu cần
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
