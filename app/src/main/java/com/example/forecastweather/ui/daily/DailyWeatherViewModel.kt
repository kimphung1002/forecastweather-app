package com.example.forecastweather.ui.daily

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastweather.data.api.Constant
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.data.api.RetrofitInstance
import com.example.forecastweather.data.api.WeatherModel
import com.example.forecastweather.data.model.ForecastResponse
import kotlinx.coroutines.launch

class DailyWeatherViewModel : ViewModel() {

    private val _currentCity = mutableStateOf("Hanoi")
    val currentCity: State<String> = _currentCity

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>?>()
    val weatherResult: MutableLiveData<NetworkResponse<WeatherModel>?> = _weatherResult

    private val _forecastResult = MutableLiveData<NetworkResponse<ForecastResponse>?>()
    val forecastResult: MutableLiveData<NetworkResponse<ForecastResponse>?> = _forecastResult

    var lastSearchedCity: String? = null
    var hasSearched: Boolean = false

    fun getData(city: String) {
        lastSearchedCity = city
        hasSearched = true
        _currentCity.value = city // cập nhật tên thành phố

        viewModelScope.launch {
            _weatherResult.value = NetworkResponse.Loading
            _forecastResult.value = NetworkResponse.Loading
            try {
                val weatherResponse = RetrofitInstance.weatherApi.getWeather(city, Constant.apiKey)
                val forecastResponse = RetrofitInstance.weatherApi.getForecast(city, Constant.apiKey)

                if (weatherResponse.isSuccessful && forecastResponse.isSuccessful) {
                    _weatherResult.value = NetworkResponse.Success(weatherResponse.body()!!)
                    _forecastResult.value = NetworkResponse.Success(forecastResponse.body()!!)
                } else {
                    _weatherResult.value = NetworkResponse.Error("Không có dữ liệu thời tiết.")
                    _forecastResult.value = NetworkResponse.Error("Không có dữ liệu dự báo.")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Lỗi: ${e.message}")
                _forecastResult.value = NetworkResponse.Error("Lỗi: ${e.message}")
            }
        }
    }

    fun reloadIfNeeded() {
        if (_weatherResult.value == null) {
            if (lastSearchedCity != null) {
                getData(lastSearchedCity!!)
            } else {
                getData("Hanoi")
            }
        }
    }

    fun resetWeatherResult() {
        _weatherResult.value = null
        _forecastResult.value = null
    }
}
