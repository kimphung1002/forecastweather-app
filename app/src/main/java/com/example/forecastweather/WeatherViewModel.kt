package com.example.forecastweather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.data.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {
            val result = repository.getWeatherData(city)
            _weatherResult.value = result

            if (result is NetworkResponse.Error) {
                Log.e("WeatherViewModel", "Error: ${result.message}")
            }
        }
    }
}
