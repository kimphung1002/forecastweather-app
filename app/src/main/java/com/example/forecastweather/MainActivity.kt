package com.example.forecastweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.forecastweather.ui.daily.DailyWeatherViewModel
import com.example.forecastweather.ui.navigation.AppNavigation
import com.example.forecastweather.ui.search.SearchViewModel
import com.example.forecastweather.ui.search.SearchViewModelFactory
import com.example.forecastweather.ui.settings.SettingsViewModel
import com.example.forecastweather.ui.settings.SettingsViewModelFactory
import com.example.forecastweather.ui.theme.RealtimeWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as ForecastWeatherApp).appContainer

        val dailyWeatherViewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]
        val settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(appContainer.settingsUseCase)
        )[SettingsViewModel::class.java]
        val searchViewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(appContainer.searchHistoryRepository)
        )[SearchViewModel::class.java]

        setContent {
            val userSettings by settingsViewModel.userSettings.collectAsState()

            val darkTheme = when (userSettings.theme) {
                "Sáng" -> false
                "Tối" -> true
                else -> isSystemInDarkTheme()
            }

            RealtimeWeatherTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = dailyWeatherViewModel,
                        settingsViewModel = settingsViewModel,
                        searchViewModel = searchViewModel
                    )
                }
            }
        }
    }
}
