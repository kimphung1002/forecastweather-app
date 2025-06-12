package com.example.forecastweather.domain.model

data class Settings(
    val temperatureUnit: String = "metric",  // "metric" cho °C, "imperial" cho °F
    val windSpeedUnit: String = "km/h",     // "km/h" hoặc "m/s"
    val is24HourFormat: Boolean = true,      // true nếu dùng định dạng 24h, false dùng 12h
    val language: String = "vi",             // mã ngôn ngữ, ví dụ "vi" hoặc "en"
    val theme: String = "auto"                // "auto", "light", "dark"
)
