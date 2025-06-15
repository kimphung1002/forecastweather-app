package com.example.forecastweather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastweather.data.api.Constant
import com.example.forecastweather.data.api.RetrofitInstance
import com.example.forecastweather.data.model.GeocodingResponseItem
import com.example.forecastweather.data.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchHistoryRepository): ViewModel() {

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history

    init {
        viewModelScope.launch {
            repository.searchHistory.collectLatest { list ->
                _history.value = list.sorted()
            }
        }
    }

    fun addCity(city: String) {
        viewModelScope.launch {
            val current = _history.value.toMutableList()
            current.remove(city) // Xoá nếu đã có (chống trùng)
            current.add(0, city) // Thêm mới lên đầu danh sách
            repository.saveAll(current) // <- gọi hàm lưu danh sách mới vào DataStore
        }
    }


    fun removeCity(city: String) {
        viewModelScope.launch {
            repository.removeCity(city)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    private val _suggestions = MutableStateFlow<List<GeocodingResponseItem>>(emptyList())
    val suggestions: StateFlow<List<GeocodingResponseItem>> = _suggestions

    fun fetchCitySuggestions(query: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.geocodingApi.getCitySuggestions(
                    query = query,
                    apiKey = Constant.apiKey // Dùng đúng tên biến và đúng tên tham số
                )
                _suggestions.value = result
            } catch (e: Exception) {
                _suggestions.value = emptyList() // hoặc xử lý lỗi
            }
        }
    }
}
