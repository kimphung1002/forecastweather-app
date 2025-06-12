package com.example.forecastweather.domain.model

data class UserSettings(
    val temperatureUnit: String = "metric", // metric / imperial
    val windUnit: String = "km/h",          // km/h, m/s, mph
    val timeFormat: String = "24h",         // 12h / 24h
    val language: String = "Tiếng Việt",    // Tiếng Việt / English
    val theme: String = "Tự động"           // Sáng / Tối / Tự động
)
