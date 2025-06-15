package com.example.forecastweather.domain.mapper

object WeatherIconMapper {

    private val iconMap = mapOf(
        "01d" to "clear_day",
        "01n" to "clear_night",
        "02d" to "partly_cloudy_day",
        "02n" to "partly_cloudy_night",
        "03d" to "cloudy",
        "03n" to "cloudy",
        "04d" to "overcast_day",
        "04n" to "overcast_night",
        "09d" to "rain",
        "09n" to "rain",
        "10d" to "extreme_day_rain",
        "10n" to "extreme_night_rain",
        "11d" to "thunderstorms_day_rain",
        "11n" to "thunderstorms_night_rain",
        "13d" to "snow",
        "13n" to "snow",
        "50d" to "fog_day",
        "50n" to "fog_night"
    )

    fun mapToLocalFileName(iconCode: String): String {
        return iconMap[iconCode] ?: "clear_day"
    }
}
