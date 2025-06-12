package com.example.forecastweather.data.api

import com.google.gson.annotations.SerializedName

data class WeatherModel(
    val coord: Coord,
    val weather: List<WeatherDesc>,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val name: String
)

data class Coord(val lon: Double, val lat: Double)

data class WeatherDesc(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
) {
    val tempInt: Int get() = temp.toInt()
    val feelsLikeInt: Int get() = feelsLike.toInt()
    val tempMinInt: Int get() = tempMin.toInt()
    val tempMaxInt: Int get() = tempMax.toInt()
}

data class Wind(val speed: Double, val deg: Int, val gust: Double?)

data class Clouds(val all: Int)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
