package com.example.forecastweather.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastweather.domain.model.UserSettings
import com.example.forecastweather.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settingsUseCase.userSettings
        .stateIn(viewModelScope, SharingStarted.Lazily, UserSettings())

    fun updateTemperatureUnit(unit: String) {
        viewModelScope.launch { settingsUseCase.setTemperatureUnit(unit) }
    }

    fun updateWindSpeedUnit(unit: String) {
        viewModelScope.launch { settingsUseCase.setWindUnit(unit) }
    }

    fun updateTimeFormat(format: String) {
        viewModelScope.launch { settingsUseCase.setTimeFormat(format) }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch { settingsUseCase.setLanguage(language) }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch { settingsUseCase.setTheme(theme) }
    }
}
