package com.example.forecastweather.data.model

import com.example.forecastweather.data.api.Main
import com.example.forecastweather.data.api.WeatherDesc
import com.example.forecastweather.data.api.Wind


data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherDesc>,
    val wind: Wind, // 👈 Thêm dòng này
    val dt_txt: String
)

data class City(
    val name: String,
    val country: String,
    val timezone: Int
)
