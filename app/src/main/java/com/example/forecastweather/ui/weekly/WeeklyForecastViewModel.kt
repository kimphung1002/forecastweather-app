package com.example.forecastweather.ui.weekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastweather.WeatherRepository
import com.example.forecastweather.data.api.NetworkResponse
import kotlinx.coroutines.launch

class WeeklyForecastViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _forecast = MutableLiveData<NetworkResponse<WeatherRepository.WeeklyForecastResult>>()
    val forecast: LiveData<NetworkResponse<WeatherRepository.WeeklyForecastResult>> = _forecast

    fun loadWeeklyForecast(city: String) {
        _forecast.value = NetworkResponse.Loading

        viewModelScope.launch {
            val result = repository.getWeeklyForecastFull(city)
            _forecast.value = result
        }
    }
}
