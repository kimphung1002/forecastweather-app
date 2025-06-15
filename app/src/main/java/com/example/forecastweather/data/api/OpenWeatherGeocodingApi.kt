package com.example.forecastweather.data.api

import com.example.forecastweather.data.model.GeocodingResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherGeocodingApi {

    @GET("geo/1.0/direct")
    suspend fun getCitySuggestions(
        @Query("q") query: String,              // Tên thành phố gõ vào
        @Query("limit") limit: Int = 5,         // Giới hạn kết quả
        @Query("appid") apiKey: String          // API Key từ OpenWeatherMap
    ): List<GeocodingResponseItem>
}
