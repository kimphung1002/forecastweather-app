package com.example.forecastweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.forecastweather.WeatherRepository
import com.example.forecastweather.ui.about.AboutScreen
import com.example.forecastweather.ui.daily.DailyWeatherScreen
import com.example.forecastweather.ui.daily.DailyWeatherViewModel
import com.example.forecastweather.ui.search.SearchViewModel
import com.example.forecastweather.ui.search.SearchWeatherScreen
import com.example.forecastweather.ui.settings.SettingScreen
import com.example.forecastweather.ui.settings.SettingsViewModel
import com.example.forecastweather.ui.weekly.WeeklyForecastScreen
import com.example.forecastweather.ui.weekly.WeeklyForecastViewModel
import com.example.forecastweather.ui.weekly.WeeklyForecastViewModelFactory

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: DailyWeatherViewModel,
    settingsViewModel: SettingsViewModel, // <-- thêm tham số mới
    searchViewModel: SearchViewModel, // ✅ Thêm dòng này
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.DAILY,
        modifier = modifier
    ) {
        composable(NavigationRoutes.DAILY) {
            DailyWeatherScreen(
                viewModel = viewModel,
                settingsViewModel = settingsViewModel, // <-- truyền tiếp xuống màn hình
                onNavigateToSearch = {
                    navController.navigate(NavigationRoutes.SEARCH)
                }
            )
        }

        composable(NavigationRoutes.SEARCH) {
            SearchWeatherScreen(
                viewModel = viewModel,
                searchViewModel = searchViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.WEEKLY) {
            val currentCity = viewModel.currentCity.value ?: "Hanoi"
            val repository = WeatherRepository()
            val weeklyViewModel: WeeklyForecastViewModel = viewModel(
                factory = WeeklyForecastViewModelFactory(repository)
            )

            WeeklyForecastScreen(
                viewModel = weeklyViewModel,
                settingsViewModel = settingsViewModel, // <-- truyền tiếp
                city = currentCity
            )
        }

        composable(NavigationRoutes.SETTINGS) {
            SettingScreen(
                settingsViewModel = settingsViewModel  // <-- truyền vào màn Setting
            )
        }

        composable(NavigationRoutes.ABOUT) {
            AboutScreen()
        }
    }
}
