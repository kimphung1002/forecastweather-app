package com.example.forecastweather.domain.usecase

import com.example.forecastweather.domain.model.UserSettings
import com.example.forecastweather.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsUseCase(private val repository: SettingsRepository) {
    val userSettings: Flow<UserSettings> = repository.userSettings

    suspend fun setTemperatureUnit(unit: String) = repository.setTemperatureUnit(unit)
    suspend fun setWindUnit(unit: String) = repository.setWindUnit(unit)
    suspend fun setTimeFormat(format: String) = repository.setTimeFormat(format)
    suspend fun setLanguage(lang: String) = repository.setLanguage(lang)
    suspend fun setTheme(theme: String) = repository.setTheme(theme)
}
