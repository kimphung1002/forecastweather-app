package com.example.forecastweather.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryRepository(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "search_history")
        private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("search_history")
    }

    val searchHistory: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[SEARCH_HISTORY_KEY]?.toList() ?: emptyList()
    }

    suspend fun addCity(city: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[SEARCH_HISTORY_KEY]?.toMutableSet() ?: mutableSetOf()
            currentSet.add(city)
            preferences[SEARCH_HISTORY_KEY] = currentSet
        }
    }

    suspend fun removeCity(city: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[SEARCH_HISTORY_KEY]?.toMutableSet() ?: mutableSetOf()
            currentSet.remove(city)
            preferences[SEARCH_HISTORY_KEY] = currentSet
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_KEY] = emptySet()
        }
    }
}
