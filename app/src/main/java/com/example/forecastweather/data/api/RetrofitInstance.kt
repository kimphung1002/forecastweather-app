package com.example.forecastweather.data.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val baseUrl = "https://api.openweathermap.org/data/2.5/"


    private fun getInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApi : WeatherApi = getInstance().create(WeatherApi::class.java)

    private const val BASE_URL = "https://api.openweathermap.org/"

    val geocodingApi: OpenWeatherGeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherGeocodingApi::class.java)
    }
}
