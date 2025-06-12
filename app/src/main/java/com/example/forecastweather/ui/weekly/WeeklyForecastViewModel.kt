package com.example.forecastweather.ui.weekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastweather.WeatherRepository
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.domain.model.DailyForecast
import kotlinx.coroutines.launch

class WeeklyForecastViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _forecast = MutableLiveData<NetworkResponse<List<DailyForecast>>>()
    val forecast: LiveData<NetworkResponse<List<DailyForecast>>> = _forecast

    fun loadWeeklyForecast(city: String) {
        _forecast.value = NetworkResponse.Loading

        viewModelScope.launch {
            val result = repository.getWeeklyForecast(city)
            _forecast.value = result
        }
    }
}
