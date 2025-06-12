package com.example.forecastweather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            repository.addCity(city)
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
}
