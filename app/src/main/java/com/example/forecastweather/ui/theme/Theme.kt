package com.example.forecastweather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = NavyBlue,
    secondary = Color(0xFF009688),
    onSecondary = Color.White,
    background = Color(0xFFF6F6F6),
    onBackground = Color.Black,
    surface = CardLightBackground,
    onSurface = Color.Black,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF80CBC4),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = CardDarkBackground,
    onSurface = Color.White,
    error = Color(0xFFEF9A9A),
    onError = Color.Black
)

// Gradient background for weather conditions
fun getWeatherGradient(condition: String): Brush {
    return when (condition.lowercase()) {
        "clear" -> Brush.linearGradient(listOf(Color(0xFF56CCF2), Color(0xFF2F80ED)))
        "clouds" -> Brush.linearGradient(listOf(Color(0xFF757F9A), Color(0xFFD7DDE8)))
        "rain" -> Brush.linearGradient(listOf(Color(0xFF314755), Color(0xFF26A0DA)))
        "snow" -> Brush.linearGradient(listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3)))
        "thunderstorm" -> Brush.linearGradient(listOf(Color(0xFF373B44), Color(0xFF4286f4)))
        "drizzle" -> Brush.linearGradient(listOf(Color(0xFF89F7FE), Color(0xFF66A6FF)))
        "mist", "fog", "haze" -> Brush.linearGradient(listOf(Color(0xFF3E5151), Color(0xFFDECBA4)))
        else -> Brush.linearGradient(listOf(Color(0xFF00C6FB), Color(0xFF005BEA)))
    }
}

// Auto text color depending on condition
fun getTextColorForWeather(condition: String): Color {
    return when (condition.lowercase()) {
        "clear", "clouds", "snow" -> Color.Black
        "rain", "drizzle", "thunderstorm", "mist", "fog", "haze" -> Color.White
        else -> Color.Black
    }
}

@Composable
fun RealtimeWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // You can customize Typography here if needed
        content = content
    )
}
