package com.example.forecastweather.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.forecastweather.domain.model.UserSettings
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {

    companion object {
        private const val DATASTORE_NAME = "user_settings"
        private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

        private val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        private val WIND_UNIT = stringPreferencesKey("wind_unit")
        private val TIME_FORMAT = stringPreferencesKey("time_format")
        private val LANGUAGE = stringPreferencesKey("language")
        private val THEME = stringPreferencesKey("theme")
    }

    val settingsFlow = context.dataStore.data.map { prefs ->
        UserSettings(
            temperatureUnit = prefs[TEMPERATURE_UNIT] ?: "metric",
            windUnit = prefs[WIND_UNIT] ?: "km/h",
            timeFormat = prefs[TIME_FORMAT] ?: "24h",
            language = prefs[LANGUAGE] ?: "Tiếng Việt",
            theme = prefs[THEME] ?: "Tự động"
        )
    }

    suspend fun updateTemperatureUnit(unit: String) {
        context.dataStore.edit { it[TEMPERATURE_UNIT] = unit }
    }

    suspend fun updateWindUnit(unit: String) {
        context.dataStore.edit { it[WIND_UNIT] = unit }
    }

    suspend fun updateTimeFormat(format: String) {
        context.dataStore.edit { it[TIME_FORMAT] = format }
    }

    suspend fun updateLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE] = lang }
    }

    suspend fun updateTheme(theme: String) {
        context.dataStore.edit { it[THEME] = theme }
    }
}
