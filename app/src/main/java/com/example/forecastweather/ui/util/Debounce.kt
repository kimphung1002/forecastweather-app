package com.example.forecastweather.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debounce<T>(
    private val coroutineScope: CoroutineScope,
    private val delayMillis: Long = 500L,
    private val onEvent: (T) -> Unit
) {
    private var job: Job? = null

    fun submit(value: T) {
        job?.cancel()
        job = coroutineScope.launch {
            delay(delayMillis)
            onEvent(value)
        }
    }
}
