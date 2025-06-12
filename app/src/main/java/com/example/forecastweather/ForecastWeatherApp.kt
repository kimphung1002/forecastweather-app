package com.example.forecastweather

import android.app.Application
import com.example.forecastweather.di.AppContainer

class ForecastWeatherApp : Application() {

    companion object {
        lateinit var instance: ForecastWeatherApp
            private set
    }

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)
    }
}
