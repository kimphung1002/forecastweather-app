package com.example.forecastweather.data.repository

import com.example.forecastweather.data.local.SettingsDataStore
import com.example.forecastweather.domain.model.UserSettings
import com.example.forecastweather.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val userSettings: Flow<UserSettings> = dataStore.settingsFlow

    override suspend fun setTemperatureUnit(unit: String) = dataStore.updateTemperatureUnit(unit)
    override suspend fun setWindUnit(unit: String) = dataStore.updateWindUnit(unit)
    override suspend fun setTimeFormat(format: String) = dataStore.updateTimeFormat(format)
    override suspend fun setLanguage(lang: String) = dataStore.updateLanguage(lang)
    override suspend fun setTheme(theme: String) = dataStore.updateTheme(theme)
}
