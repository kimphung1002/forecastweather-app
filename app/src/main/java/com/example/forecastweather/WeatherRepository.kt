package com.example.forecastweather


import com.example.forecastweather.data.api.Constant
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.data.api.RetrofitInstance
import com.example.forecastweather.data.api.WeatherModel
import com.example.forecastweather.data.model.ForecastResponse
import com.example.forecastweather.domain.mapper.ForecastMapper
import com.example.forecastweather.domain.model.DailyForecast

class WeatherRepository {

    private val weatherApi = RetrofitInstance.weatherApi

    suspend fun getWeatherData(city: String): NetworkResponse<WeatherModel> {
        return try {
            val response = weatherApi.getWeather(
                cityName = city,
                apikey = Constant.apiKey,
                units = "metric",
                lang = "vi"
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResponse.Success(it)
                } ?: NetworkResponse.Error("Empty response body")
            } else {
                NetworkResponse.Error("Error code: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResponse.Error("Network failure: ${e.message}")
        }
    }

    suspend fun getForecastData(city: String): NetworkResponse<ForecastResponse> {
        return try {
            val response = weatherApi.getForecast(
                cityName = city,
                apiKey = Constant.apiKey,
                units = "metric",
                lang = "vi"
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResponse.Success(it)
                } ?: NetworkResponse.Error("Empty response body")
            } else {
                NetworkResponse.Error("Error code: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResponse.Error("Network failure: ${e.message}")
        }
    }

    suspend fun getWeeklyForecast(city: String): NetworkResponse<List<DailyForecast>> {
        return try {
            val weatherResponse = weatherApi.getWeather(city, Constant.apiKey)
            val forecastResponse = weatherApi.getForecast(city, Constant.apiKey)

            if (forecastResponse.isSuccessful && weatherResponse.isSuccessful) {
                val forecastBody = forecastResponse.body()
                val weatherBody = weatherResponse.body()

                if (forecastBody != null && weatherBody != null) {
                    val data = ForecastMapper.mapToDailyForecast(
                        forecastBody,
                        weatherBody.sys.sunrise,
                        weatherBody.sys.sunset
                    )
                    NetworkResponse.Success(data)
                } else {
                    NetworkResponse.Error("Empty response body")
                }
            } else {
                NetworkResponse.Error("Error code: forecast ${forecastResponse.code()}")
            }
        } catch (e: Exception) {
            NetworkResponse.Error("Network failure: ${e.message}")
        }
    }

}
