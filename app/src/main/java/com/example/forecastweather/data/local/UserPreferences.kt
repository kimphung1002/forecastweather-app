package com.example.forecastweather.data.local

data class UserPreferences(
    val themeMode: String = "System",  // Light, Dark, System
    val isCelsius: Boolean = true
)
