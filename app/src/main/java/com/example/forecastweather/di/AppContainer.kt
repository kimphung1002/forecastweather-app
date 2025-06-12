package com.example.forecastweather.di

import android.content.Context
import com.example.forecastweather.data.local.SettingsDataStore
import com.example.forecastweather.data.repository.SearchHistoryRepository
import com.example.forecastweather.data.repository.SettingsRepositoryImpl
import com.example.forecastweather.domain.usecase.SettingsUseCase

class AppContainer(context: Context) {

    // Settings UseCase
    private val settingsDataStore = SettingsDataStore(context)
    private val settingsRepository = SettingsRepositoryImpl(settingsDataStore)
    val settingsUseCase = SettingsUseCase(settingsRepository)

    // Search History Repository
    val searchHistoryRepository = SearchHistoryRepository(context)
}
