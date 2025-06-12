package com.example.forecastweather.domain.repository

import com.example.forecastweather.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userSettings: Flow<UserSettings>
    suspend fun setTemperatureUnit(unit: String)
    suspend fun setWindUnit(unit: String)
    suspend fun setTimeFormat(format: String)
    suspend fun setLanguage(lang: String)
    suspend fun setTheme(theme: String)
}
